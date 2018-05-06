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
package org.smartloli.kafka.game.x.book_5;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

/**
 * 实现一个手动分配分区的消费者实例.
 * 
 * @author smartloli.
 *
 *         Created by May 6, 2018
 */
public class JConsumerAssign extends Thread {

	public static void main(String[] args) {
		JConsumerAssign jconsumer = new JConsumerAssign();
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
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

	/** 实现一个单线程消费者程序. */
	@Override
	public void run() {

		// 创建一个消费者实例对象
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configure());
		// 设置自定义分区
		TopicPartition tp = new TopicPartition("test_kafka_game_x", 0);
		// 手动分配
		consumer.assign(Collections.singleton(tp));
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
