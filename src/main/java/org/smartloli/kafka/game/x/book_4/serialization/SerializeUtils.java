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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 封装一个序列化的工具类.
 * 
 * @author smartloli.
 *
 *         Created by Apr 30, 2018
 */
public class SerializeUtils {

	/** 实现序列化的逻辑. */
	public static byte[] serialize(Object object) {
		ObjectOutputStream outs = null;
		ByteArrayOutputStream byteOuts = null;
		try {
			// 序列化
			byteOuts = new ByteArrayOutputStream();// 实例化字节数组
			outs = new ObjectOutputStream(byteOuts);// 实例化对象输出流
			outs.writeObject(object);// 写入对象
			byte[] bytes = byteOuts.toByteArray();
			return bytes;// 返回字节数组
		} catch (Exception e) {
			e.printStackTrace(); // 抛出异常信息
		}
		return null;
	}

	/** 实现反序列化的逻辑. */
	@SuppressWarnings("unchecked")
	public static <T> Object deserialize(byte[] bytes, Class<T> className) {
		ByteArrayInputStream bais = null;
		T tmpObject = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			tmpObject = (T) ois.readObject();
			return tmpObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
