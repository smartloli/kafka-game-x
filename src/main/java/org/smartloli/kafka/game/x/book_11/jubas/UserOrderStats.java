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
package org.smartloli.kafka.game.x.book_11.jubas;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import scala.Tuple2;

/**
 * 使用Spark引擎来统计用户订单主题中的金额.
 * 
 * @author smartloli.
 *
 *         Created by Jul 14, 2018
 */
public class UserOrderStats {

	public static void main(String[] args) throws Exception {

		if (args.length < 1) { // 设置数据源输入参数
			System.err.println("Usage: GroupId <file>");
			System.exit(1);
		}

		String bootStrapServers = "dn1:9092,dn2:9092,dn3:9092";
		String topic = "user_order_stream";
		String groupId = args[0];
		SparkConf sparkConf = new SparkConf().setMaster("yarn-client").setAppName("UserOrder");
		// 实例化一个SparkContext对象, 用来打印日志信息到控制台, 便于调试
		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		sc.setLogLevel("WARN");

		// 创建一个流对象, 设置窗口时间为10秒
		JavaStreamingContext jssc = new JavaStreamingContext(sc, Durations.seconds(10));
		JavaInputDStream<ConsumerRecord<Object, Object>> streams = KafkaUtils.createDirectStream(jssc, LocationStrategies.PreferConsistent(), ConsumerStrategies.Subscribe(Arrays.asList(topic), configure(groupId, bootStrapServers)));

		// 将Kafka主题(user_order_stream)中的消息转化成键值对(key/value)形式
		JavaPairDStream<Integer, Long> moneys = streams.mapToPair(new PairFunction<ConsumerRecord<Object, Object>, Integer, Long>() {
			/** 序列号ID. */
			private static final long serialVersionUID = 1L;

			@Override
			public Tuple2<Integer, Long> call(ConsumerRecord<Object, Object> t) throws Exception {
				JSONObject object = JSON.parseObject(t.value().toString());
				return new Tuple2<Integer, Long>(object.getInteger("plat"), object.getLong("money"));
			}
		}).reduceByKey(new Function2<Long, Long, Long>() {
			/** 序列号ID. */
			private static final long serialVersionUID = 1L;

			@Override
			public Long call(Long v1, Long v2) throws Exception {
				return v1 + v2; // 通过平台号(plat)进行分组聚合
			}
		});

		// 调试
		moneys.foreachRDD(rdd -> {
			rdd.collect();
		});

		// 将统计结果存储到MySQL数据库
		moneys.foreachRDD(rdd -> {
			Connection connection = MySQLPool.getConnection();
			Statement stmt = connection.createStatement();
			rdd.collect().forEach(line -> {
				int plat = line._1.intValue();
				long total = line._2.longValue();
				String sql = String.format("insert into `user_order` (`plat`, `total`) values (%s, %s)", plat, total);
				try {
					// 调用MySQL工具类, 将统计结果组装成SQL语句写入到MySQL数据库
					stmt.executeUpdate(sql); 
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});

			MySQLPool.release(connection); // 是否MySQL连接对象到连接队列
		});

		jssc.start();	// 开始计算
		try {
			jssc.awaitTermination(); // 等待计算结束
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			jssc.close();	// 发生异常, 关闭流操作对象
		}
	}

	/** 初始化Kafka集群信息. */
	private static Map<String, Object> configure(String group, String brokers) {
		Map<String, Object> props = new HashMap<>();
		props.put("bootstrap.servers", brokers);// 指定Kafka集群地址
		props.put("group.id", group);// 指定消费者组
		props.put("enable.auto.commit", "true");// 开启自动提交
		props.put("auto.commit.interval.ms", "1000");// 自动提交的时间间隔
		// 反序列化消息主键
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		// 反序列化消费记录
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

}
