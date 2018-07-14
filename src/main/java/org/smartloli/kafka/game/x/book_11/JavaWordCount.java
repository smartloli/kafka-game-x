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
package org.smartloli.kafka.game.x.book_11;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;

import scala.Tuple2;

/**
 * 统计单词出现频率.
 * 
 * @author smartloli.
 *
 *         Created by Jul 11, 2018
 */
public class JavaWordCount {
	private static final Pattern SPACE = Pattern.compile(" "); // 声明一个分割对象

	public static void main(String[] args) throws Exception {

		if (args.length < 1) { // 设置数据源输入参数
			System.err.println("Usage: JavaWordCount <file>");
			System.exit(1);
		}

		SparkSession spark = SparkSession.builder().appName("JavaWordCount").getOrCreate(); // 实例化一个Spark会话对象

		JavaRDD<String> lines = spark.read().textFile(args[0]).javaRDD(); // 读取数据源

		JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator()); // 根据空格分割单词

		JavaPairRDD<String, Integer> ones = words.mapToPair(s -> new Tuple2<>(s, 1)); // 将分割后的单词逐一输出

		JavaPairRDD<String, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2); // 按照相同单词进行合并累加

		List<Tuple2<String, Integer>> output = counts.collect(); // 输出结果集
		for (Tuple2<?, ?> tuple : output) {
			System.out.println(tuple._1() + ": " + tuple._2()); // 循环打印统计的单词频率结果
		}
		spark.stop(); // 关闭Spark会话对象
	}
}
