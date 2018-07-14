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
import java.sql.DriverManager;
import java.util.LinkedList;

/**
 * 实现一个MySQL工具类.
 * 
 * @author smartloli.
 *
 *         Created by Jul 15, 2018
 */
public class MySQLPool {
	private static LinkedList<Connection> queues; // 声明一个连接队列

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 加载MySQL驱动
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 初始化MySQL连接对象
	public synchronized static Connection getConnection() {
		try {
			if (queues == null) {
				queues = new LinkedList<Connection>();
				for (int i = 0; i < 5; i++) {
					Connection conn = DriverManager.getConnection("jdbc:mysql://nna:3306/game", "root", "123456");
					queues.push(conn);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queues.poll();

	}

	/** 释放MySQL连接对象到连接队列. */
	public static void release(Connection conn) {
		queues.push(conn);
	}
}
