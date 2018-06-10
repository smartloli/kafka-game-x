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
package org.smartloli.kafka.game.x.book_8;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author smartloli.
 *
 *         Created by Jun 8, 2018
 */
public class FileStreamSinkTask extends SinkTask {

	private final Logger LOG = LoggerFactory.getLogger(FileStreamSinkTask.class);
	private String filename;
	private PrintStream ps;

	/** 获取版本信息. */
	public String version() {
		return new FileStreamSourceConnector().version();
	}

	/** 持久化实现逻辑. */
	public void put(Collection<SinkRecord> records) {
		for (SinkRecord record : records) {
			ps.println("FileStream file connect: ");
			ps.println(record.value());
		}
	}

	/** 初始化. */
	public void start(Map<String, String> props) {
		filename = props.get(FileStreamSourceConnector.FILE_CONFIG);
		if (filename == null) {
			ps = System.out;
		} else {
			try {
				ps = new PrintStream(new FileOutputStream(filename, true), false, StandardCharsets.UTF_8.name());
			} catch (Exception e) {
				LOG.info("Couldn't find or create file, error msg is " + e.getMessage());
			}
		}
	}

	/** 关闭文件. */
	public void stop() {
		if (ps != null && ps != System.out) {
			ps.close();
		}
	}

	/** 持久化所有的记录到指定的主题分区中. */
	public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
		ps.flush();
	}

}
