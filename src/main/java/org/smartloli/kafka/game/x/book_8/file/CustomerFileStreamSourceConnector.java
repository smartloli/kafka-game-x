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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;

/**
 * 与控制台一起工作的非常简单的连接器, 此连接器支持Source模式和Sink模式.
 * 
 * @author smartloli.
 *
 *         Created by Jun 8, 2018
 */
public class CustomerFileStreamSourceConnector extends SourceConnector {
	// 定义主题配置变量
	public static final String TOPIC_CONFIG = "topic";
	// 定义文件配置变量
	public static final String FILE_CONFIG = "file";

	// 实例化一个配置对象
	private static final ConfigDef CONFIG_DEF = new ConfigDef().define(FILE_CONFIG, Type.STRING, Importance.HIGH, "Source filename.").define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, "The topic to publish data to");

	// 声明文件名变量
	private String filename;
	// 声明主题变量
	private String topic;

	/** 获取版本. */
	public String version() {
		return AppInfoParser.getVersion();
	}

	/** 开始初始化. */
	public void start(Map<String, String> props) {
		filename = props.get(FILE_CONFIG);
		topic = props.get(TOPIC_CONFIG);
		if (topic == null || topic.isEmpty())
			throw new ConnectException("FileStreamSourceConnector configuration must include 'topic' setting");
		if (topic.contains(","))
			throw new ConnectException("FileStreamSourceConnector should only have a single topic when used as a source.");
	}

	/** 实例化输入类. */
	public Class<? extends Task> taskClass() {
		return CustomerFileStreamSourceTask.class;
	}

	/** 获取配置信息. */
	public List<Map<String, String>> taskConfigs(int maxTasks) {
		ArrayList<Map<String, String>> configs = new ArrayList<>();
		Map<String, String> config = new HashMap<>();
		if (filename != null)
			config.put(FILE_CONFIG, filename);
		config.put(TOPIC_CONFIG, topic);
		configs.add(config);
		return configs;
	}

	@Override
	public void stop() {
	}

	/** 获取配置对象. */
	public ConfigDef config() {
		return CONFIG_DEF;
	}
}
