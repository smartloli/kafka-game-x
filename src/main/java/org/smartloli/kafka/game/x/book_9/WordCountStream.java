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
package org.smartloli.kafka.game.x.book_9;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.ValueMapper;

/**
 * 使用高阶KStream DSL统计一个流数据单词频率.
 * 
 * @author smartloli.
 *
 *         Created by Jun 19, 2018
 */
public class WordCountStream {
	public static void main(String[] args) throws Exception {
		Properties props = new Properties(); // 实例化一个属性对象
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kstreams_wordcount"); // 配置一个应用ID
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dn1:9092,dn2:9092,dn3:9092"); // 配置Kafka集群地址
		props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName()); // 设置序列化与反序列类键属性
		props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName()); // 设置序列化与反序列类值属性

		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 设置偏移量重置属性

		KStreamBuilder builder = new KStreamBuilder(); // 实例化一个流处理构建对象

		KStream<String, String> source = builder.stream("streams_wordcount_input"); // 指定一个输入流主题

		// 执行统计单词逻辑
		KTable<String, Long> counts = source.flatMapValues(new ValueMapper<String, Iterable<String>>() {
			@Override
			public Iterable<String> apply(String value) {
				return Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" "));
			}
		}).map(new KeyValueMapper<String, String, KeyValue<String, String>>() {
			@Override
			public KeyValue<String, String> apply(String key, String value) {
				return new KeyValue<>(value, value);
			}
		}).groupByKey().count("counts");

		counts.print(); // 输出统计结果

		KafkaStreams streams = new KafkaStreams(builder, props); // 实例化一个流处理对象
		streams.start(); // 执行流处理
	}
}
