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
package org.smartloli.kafka.game.x.book_4.serialization;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义序列化, 发送消息给Kafka.
 * 
 * @author smartloli.
 *
 *         Created by Apr 30, 2018
 */
public class JProducerSerial extends Thread {

	private static Logger LOG = LoggerFactory.getLogger(JProducerSerial.class);

	/** 配置Kafka连接信息. */
	public Properties configure() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "dn1:9092,dn2:9092,dn3:9092");// 指定Kafka集群地址
		props.put("acks", "1"); // 设置应答模式, 1表示有一个Kafka代理节点返回结果
		props.put("retries", 0); // 重试次数
		props.put("batch.size", 16384); // 批量提交大小
		props.put("linger.ms", 1); // 延时提交
		props.put("buffer.memory", 33554432); // 缓冲大小
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // 序列化主键
		props.put("value.serializer", "org.smartloli.kafka.game.x.book_4.serialization.JSalarySeralizer");// 自定义序列化值

		return props;
	}

	public static void main(String[] args) {
		JProducerSerial producer = new JProducerSerial();
		producer.start();
	}

	/** 实现一个单线程生产者客户端. */
	public void run() {
		Producer<String, JSalarySerial> producer = new KafkaProducer<>(configure());
		JSalarySerial jss = new JSalarySerial();
		jss.setId("2018");
		jss.setSalary("100");

		producer.send(new ProducerRecord<String, JSalarySerial>("test_topic_ser_des", "key", jss), new Callback() {
			public void onCompletion(RecordMetadata metadata, Exception e) {
				if (e != null) {
					LOG.error("Send error, msg is " + e.getMessage());
				} else {
					LOG.info("The offset of the record we just sent is: " + metadata.offset());
				}
			}
		});

		try {
			sleep(3000);// 间隔3秒
		} catch (InterruptedException e) {
			LOG.error("Interrupted thread error, msg is " + e.getMessage());
		}

		producer.close();// 关闭生产者对象
	}
}
