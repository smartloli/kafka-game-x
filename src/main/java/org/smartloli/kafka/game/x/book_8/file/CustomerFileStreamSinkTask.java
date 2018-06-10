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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过CustomerFileStreamSinkTask将记录进行标准输出或者写文件.
 * 
 * @author smartloli.
 *
 *         Created by Jun 8, 2018
 */
public class CustomerFileStreamSinkTask extends SinkTask {
	// 声明一个日志对象
	private static final Logger LOG = LoggerFactory.getLogger(CustomerFileStreamSinkTask.class);

	// 声明一个文件名变量
	private String filename;
	// 声明一个输出流对象
	private PrintStream outputStream;

	/** 构造函数. */
	public CustomerFileStreamSinkTask() {
	}

	/** 重载构造函数. */
	public CustomerFileStreamSinkTask(PrintStream outputStream) {
		filename = null;
		this.outputStream = outputStream;
	}

	/** 获取版本号. */
	public String version() {
		return new CustomerFileStreamSinkConnector().version();
	}

	/** 开始执行任务. */
	public void start(Map<String, String> props) {
		filename = props.get(CustomerFileStreamSinkConnector.FILE_CONFIG);
		if (filename == null) {
			outputStream = System.out;
		} else {
			try {
				outputStream = new PrintStream(new FileOutputStream(filename, true), false, StandardCharsets.UTF_8.name());
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				throw new ConnectException("Couldn't find or create file for FileStreamSinkTask", e);
			}
		}
	}

	/** 发送记录给Sink并输出. */
	public void put(Collection<SinkRecord> sinkRecords) {
		for (SinkRecord record : sinkRecords) {
			LOG.trace("Writing line to {}: {}", logFilename(), record.value());
			outputStream.println(record.value());
		}
	}

	/** 持久化数据. */
	public void flush(Map<TopicPartition, OffsetAndMetadata> offsets) {
		LOG.trace("Flushing output stream for {}", logFilename());
		outputStream.flush();
	}

	/** 停止任务. */
	public void stop() {
		if (outputStream != null && outputStream != System.out)
			outputStream.close();
	}

	/** 判断是标准输出还是文件写入. */
	private String logFilename() {
		return filename == null ? "stdout" : filename;
	}
}
