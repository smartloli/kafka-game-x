/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartloli.kafka.game.x.book_8.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过CustomerFileStreamSourceTask从标准输入或者文件读取.
 * 
 * @author smartloli.
 *
 *         Created by Jun 8, 2018
 */
public class CustomerFileStreamSourceTask extends SourceTask {
	// 声明一个日志类
	private static final Logger LOG = LoggerFactory.getLogger(CustomerFileStreamSourceTask.class);
	// 定义文件字段
	public static final String FILENAME_FIELD = "filename";
	// 定义偏移量字段
	public static final String POSITION_FIELD = "position";
	// 定义值的值的数据格式
	private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

	// 声明文件名
	private String filename;
	// 声明输入流对象
	private InputStream stream;
	// 声明读取对象
	private BufferedReader reader = null;
	// 定义缓冲区大小
	private char[] buffer = new char[1024];
	// 声明偏移量变量
	private int offset = 0;
	// 声明主题名
	private String topic = null;

	// 声明输入流偏移量
	private Long streamOffset;

	/** 获取版本. */
	public String version() {
		return new CustomerFileStreamSourceConnector().version();
	}

	/** 开始执行任务. */
	public void start(Map<String, String> props) {
		filename = props.get(CustomerFileStreamSourceConnector.FILE_CONFIG);
		if (filename == null || filename.isEmpty()) {
			stream = System.in;
			streamOffset = null;
			reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		}
		topic = props.get(CustomerFileStreamSourceConnector.TOPIC_CONFIG);
		if (topic == null)
			throw new ConnectException("FileStreamSourceTask config missing topic setting");
	}

	/** 读取记录并返回数据集. */
	public List<SourceRecord> poll() throws InterruptedException {
		if (stream == null) {
			try {
				stream = new FileInputStream(filename);
				Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(FILENAME_FIELD, filename));
				if (offset != null) {
					Object lastRecordedOffset = offset.get(POSITION_FIELD);
					if (lastRecordedOffset != null && !(lastRecordedOffset instanceof Long))
						throw new ConnectException("Offset position is the incorrect type");
					if (lastRecordedOffset != null) {
						LOG.debug("Found previous offset, trying to skip to file offset {}", lastRecordedOffset);
						long skipLeft = (Long) lastRecordedOffset;
						while (skipLeft > 0) {
							try {
								long skipped = stream.skip(skipLeft);
								skipLeft -= skipped;
							} catch (IOException e) {
								LOG.error("Error while trying to seek to previous offset in file: ", e);
								throw new ConnectException(e);
							}
						}
						LOG.debug("Skipped to offset {}", lastRecordedOffset);
					}
					streamOffset = (lastRecordedOffset != null) ? (Long) lastRecordedOffset : 0L;
				} else {
					streamOffset = 0L;
				}
				reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
				LOG.debug("Opened {} for reading", logFilename());
			} catch (FileNotFoundException e) {
				LOG.warn("Couldn't find file {} for FileStreamSourceTask, sleeping to wait for it to be created", logFilename());
				synchronized (this) {
					this.wait(1000);
				}
				return null;
			}
		}

		try {
			final BufferedReader readerCopy;
			synchronized (this) {
				readerCopy = reader;
			}
			if (readerCopy == null)
				return null;

			ArrayList<SourceRecord> records = null;

			int nread = 0;
			while (readerCopy.ready()) {
				nread = readerCopy.read(buffer, offset, buffer.length - offset);
				LOG.trace("Read {} bytes from {}", nread, logFilename());

				if (nread > 0) {
					offset += nread;
					if (offset == buffer.length) {
						char[] newbuf = new char[buffer.length * 2];
						System.arraycopy(buffer, 0, newbuf, 0, buffer.length);
						buffer = newbuf;
					}

					String line;
					do {
						line = extractLine();
						if (line != null) {
							LOG.trace("Read a line from {}", logFilename());
							if (records == null)
								records = new ArrayList<>();
							records.add(new SourceRecord(offsetKey(filename), offsetValue(streamOffset), topic, null, null, null, VALUE_SCHEMA, line, System.currentTimeMillis()));
						}
					} while (line != null);
				}
			}

			if (nread <= 0)
				synchronized (this) {
					this.wait(1000);
				}

			return records;
		} catch (IOException e) {
		}
		return null;
	}

	/** 解析一条记录. */
	private String extractLine() {
		int until = -1, newStart = -1;
		for (int i = 0; i < offset; i++) {
			if (buffer[i] == '\n') {
				until = i;
				newStart = i + 1;
				break;
			} else if (buffer[i] == '\r') {
				if (i + 1 >= offset)
					return null;

				until = i;
				newStart = (buffer[i + 1] == '\n') ? i + 2 : i + 1;
				break;
			}
		}

		if (until != -1) {
			String result = new String(buffer, 0, until);
			System.arraycopy(buffer, newStart, buffer, 0, buffer.length - newStart);
			offset = offset - newStart;
			if (streamOffset != null)
				streamOffset += newStart;
			return result;
		} else {
			return null;
		}
	}

	/** 停止任务. */
	public void stop() {
		LOG.trace("Stopping");
		synchronized (this) {
			try {
				if (stream != null && stream != System.in) {
					stream.close();
					LOG.trace("Closed input stream");
				}
			} catch (IOException e) {
				LOG.error("Failed to close FileStreamSourceTask stream: ", e);
			}
			this.notify();
		}
	}

	private Map<String, String> offsetKey(String filename) {
		return Collections.singletonMap(FILENAME_FIELD, filename);
	}

	private Map<String, Long> offsetValue(Long pos) {
		return Collections.singletonMap(POSITION_FIELD, pos);
	}

	/** 判断是标准输入还是读取文件. */
	private String logFilename() {
		return filename == null ? "stdin" : filename;
	}
}
