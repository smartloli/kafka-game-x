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

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实现一个序列化的类.
 * 
 * @author smartloli.
 *
 *         Created by Apr 30, 2018
 */
public class JObjectSerial implements Serializable {

	private static Logger LOG = LoggerFactory.getLogger(JObjectSerial.class);

	/**
	 * 序列化版本ID.
	 */
	private static final long serialVersionUID = 1L;

	public byte id = 1; // 用户ID
	public byte money = 100; // 充值金额

	/** 实例化入口函数. */
	public static void main(String[] args) {
		try {
			FileOutputStream fos = new FileOutputStream("/tmp/salary.out"); // 实例化一个输出流对象
			ObjectOutputStream oos = new ObjectOutputStream(fos);// 实例化一个对象输出流
			JObjectSerial jos = new JObjectSerial(); // 实例化序列化类
			oos.writeObject(jos); // 写入对象
			oos.flush(); // 刷新数据流
			oos.close();// 关闭连接
		} catch (Exception e) {
			LOG.error("Serial has error, msg is " + e.getMessage());// 打印异常信息
		}
	}

}
