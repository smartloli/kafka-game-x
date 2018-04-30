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

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

/**
 * 自定义序列化实现.
 * 
 * @author smartloli.
 *
 *         Created by Apr 30, 2018
 */
public class JSalarySeralizer implements Serializer<JSalarySerial> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {

	}

	/** 实现自定义序列化. */
	@Override
	public byte[] serialize(String topic, JSalarySerial data) {
		return SerializeUtils.serialize(data);
	}

	@Override
	public void close() {

	}

}
