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
package org.smartloli.kafka.game.x.book_4;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 实现一个生产者客户端应用程序.
 * 
 * @author smartloli.
 *
 *         Created by Apr 27, 2018
 */
public class JProducerThread extends Thread {

	// 创建一个日志对象
	private final Logger LOG = LoggerFactory.getLogger(JProducerThread.class);
	// 声明最大线程数
	private final static int MAX_THREAD_SIZE = 6;

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
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");// 序列化值
		props.put("partitioner.class", "org.smartloli.kafka.game.x.book_4.JPartitioner");// 指定自定义分区类
		
		return props;
	}

	public static void main(String[] args) {
		// 创建一个固定线程数量的线程池
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_SIZE);
		// 提交任务
		executorService.submit(new JProducerThread());
		// 关闭线程池
		executorService.shutdown();
	}

	/** 实现一个单线程生产者客户端. */
	public void run() {
		Producer<String, String> producer = new KafkaProducer<>(configure());
		// 发送100条JSON格式的数据
		for (int i = 0; i < 10; i++) {
			// 封装JSON格式
			JSONObject json = new JSONObject();
			json.put("id", i);
			json.put("ip", "192.168.0." + i);
			json.put("date", new Date().toString());
			String k = "key" + i;
			// 异步发送
			producer.send(new ProducerRecord<String, String>("ip_login_rt", k, json.toJSONString()), new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null) {
						LOG.error("Send error, msg is " + e.getMessage());
					} else {
						LOG.info("The offset of the record we just sent is: " + metadata.offset());
					}
				}
			});
		}
		try {
			sleep(3000);// 间隔3秒
		} catch (InterruptedException e) {
			LOG.error("Interrupted thread error, msg is " + e.getMessage());
		}

		producer.close();// 关闭生产者对象
	}

}
