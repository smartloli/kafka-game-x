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
import java.io.ObjectInputStream;

/**
 * 封装一个序列化的工具类.
 * 
 * @author smartloli.
 *
 *         Created by Apr 30, 2018
 */
public class SerializeUtils {

	/** 实现序列化. */
	public static byte[] serialize(Object object) {
		try {
			return object.toString().getBytes("UTF8");// 返回字节数组
		} catch (Exception e) {
			e.printStackTrace(); // 抛出异常信息
		}
		return null;
	}

	/** 实现反序列化. */
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
