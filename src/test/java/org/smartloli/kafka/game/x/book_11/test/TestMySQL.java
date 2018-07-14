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
package org.smartloli.kafka.game.x.book_11.test;

import java.sql.Connection;
import java.sql.Statement;

import org.smartloli.kafka.game.x.book_11.jubas.MySQLPool;

/**
 * TODO
 * 
 * @author smartloli.
 *
 *         Created by Jul 15, 2018
 */
public class TestMySQL {
	public static void main(String[] args) throws Exception {
		Connection connection = MySQLPool.getConnection();
		Statement stmt = connection.createStatement();
		String sql = String.format("insert into `user_order` (`plat`, `total`) values (%s, %s)", 201, 23411);
		stmt.executeUpdate(sql);
		MySQLPool.release(connection);
	}
}
