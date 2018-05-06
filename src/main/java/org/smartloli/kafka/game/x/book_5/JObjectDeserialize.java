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

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.game.x.book_4.JObjectSerial;

/**
 * 反序列化一个类.
 * 
 * @author smartloli.
 *
 *         Created by May 6, 2018
 */
public class JObjectDeserialize {

	/** 创建一个日志对象实例. */
	private static Logger LOG = LoggerFactory.getLogger(JObjectSerial.class);

	/** 实例化入口函数. */
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream("/tmp/salary.out"); // 实例化一个输入流对象
			JObjectSerial jos = (JObjectSerial) new ObjectInputStream(fis).readObject();// 反序列化还原对象
			LOG.info("ID : " + jos.id + " , Money : " + jos.money);// 打印反序列化还原后的对象属性
		} catch (Exception e) {
			LOG.error("Deserial has error, msg is " + e.getMessage());// 打印异常信息
		}
	}

}
