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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多线程消费者实例.
 * 
 * @author smartloli.
 *
 *         Created by May 6, 2018
 */
public class JConsumerMutil {

	// 创建一个日志对象
	private final static Logger LOG = LoggerFactory.getLogger(JConsumerMutil.class);
	private final KafkaConsumer<String, String> consumer; // 声明一个消费者实例
	private ExecutorService executorService; // 声明一个线程池接口

	public JConsumerMutil() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "dn1:9095,dn2:9094,dn3:9092");// 指定Kafka集群地址
		props.put("group.id", "ke");// 指定消费者组
		props.put("enable.auto.commit", "true");// 开启自动提交
		props.put("auto.commit.interval.ms", "1000");// 自动提交的时间间隔
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");// 反序列化消息主键
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");// 反序列化消费记录
		consumer = new KafkaConsumer<String, String>(props);// 实例化消费者对象
		consumer.subscribe(Arrays.asList("kv3_topic"));// 订阅消费者主题
	}

	/** 执行多线程消费者实例. */
	public void execute() {
		// 初始化线程池
		executorService = Executors.newFixedThreadPool(6);
		while (true) {
			// 拉取Kafka主题消息数据
			ConsumerRecords<String, String> records = consumer.poll(100);
			if (null != records) {
				executorService.submit(new KafkaConsumerThread(records, consumer));
			}
		}
	}

	/** 关闭消费者实例对象和线程池 */
	public void shutdown() {
		try {
			if (consumer != null) {
				consumer.close();
			}
			if (executorService != null) {
				executorService.shutdown();
			}
			if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
				LOG.error("Shutdown kafka consumer thread timeout.");
			}
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}

	/** 消费者线程实例. */
	class KafkaConsumerThread implements Runnable {

		private ConsumerRecords<String, String> records;

		public KafkaConsumerThread(ConsumerRecords<String, String> records, KafkaConsumer<String, String> consumer) {
			this.records = records;
		}

		@Override
		public void run() {
			for (TopicPartition partition : records.partitions()) {
				// 获取消费记录数据集
				List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
				LOG.info("Thread id : "+Thread.currentThread().getId());
				// 打印消费记录
				for (ConsumerRecord<String, String> record : partitionRecords) {
					System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
				}
			}
		}

	}

	/** 多线程消费者实例入口. */
	public static void main(String[] args) {
		JConsumerMutil consumer = new JConsumerMutil();
		try {
			consumer.execute();
		} catch (Exception e) {
			LOG.error("Mutil consumer from kafka has error,msg is " + e.getMessage());
			consumer.shutdown();
		}
	}
}
