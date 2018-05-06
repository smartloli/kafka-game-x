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
package org.smartloli.kafka.game.x.book_5.deserialize;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * 实现一个消费者实例代码.
 * 
 * @author smartloli.
 *
 *         Created by May 6, 2018
 */
public class JConsumerDeserialize extends Thread {

	/** 自定义序列化消费者实例入口. */
	public static void main(String[] args) {
		JConsumerDeserialize jconsumer = new JConsumerDeserialize();
		jconsumer.start();
	}

	/** 初始化Kafka集群信息. */
	private Properties configure() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "dn1:9092,dn2:9092,dn3:9092");// 指定Kafka集群地址
		props.put("group.id", "ke");// 指定消费者组
		props.put("enable.auto.commit", "true");// 开启自动提交
		props.put("auto.commit.interval.ms", "1000");// 自动提交的时间间隔
		// 反序列化消息主键
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		// 反序列化消费记录
		props.put("value.deserializer", "org.smartloli.kafka.game.x.book_5.deserialize.JSalaryDeserializer");
		return props;
	}

	/** 实现一个单线程消费者. */
	@Override
	public void run() {
		// 创建一个消费者实例对象
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configure());
		// 订阅消费主题集合
		consumer.subscribe(Arrays.asList("test_topic_ser_des"));
		// 实时消费标识
		boolean flag = true;
		while (flag) {
			// 获取主题消息数据
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records)
				// 循环打印消息记录
				System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
		}
		// 出现异常关闭消费者对象
		consumer.close();
	}

}
