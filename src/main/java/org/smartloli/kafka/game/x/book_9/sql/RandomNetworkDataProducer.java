package org.smartloli.kafka.game.x.book_9.sql;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.game.x.book_9.sql.model.NetworkData;
import org.smartloli.kafka.game.x.book_9.sql.model.NetworkSignal;
import org.smartloli.kafka.game.x.book_9.sql.util.ConfigUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 初始化生产者实例并随机生成数据.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class RandomNetworkDataProducer implements Runnable {

	private static final long INCOMING_DATA_INTERVAL = 500;

	private static final Logger LOGGER = LoggerFactory.getLogger(RandomNetworkDataProducer.class);

	@Override
	public void run() {
		LOGGER.info("Initializing kafka producer...");

		Properties properties = ConfigUtil.getConfig("network-data");
		properties.put("key.serializer", StringSerializer.class);
		properties.put("value.serializer", StringSerializer.class);
		properties.put("acks", "all");
		properties.put("retries", 0);

		String topic = properties.getProperty("topic.names");
		LOGGER.info("Start producing random network data to topic: " + topic);

		Producer<String, String> producer = new KafkaProducer<>(properties);

		Random random = new Random();

		final int deviceCount = 100;
		List<String> deviceIds = IntStream.range(0, deviceCount).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toList());

		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			NetworkData networkData = new NetworkData();

			networkData.setDeviceId(deviceIds.get(random.nextInt(deviceCount - 1)));
			networkData.setSignals(new ArrayList<>());
			for (int j = 0; j < random.nextInt(4) + 1; j++) {
				NetworkSignal networkSignal = new NetworkSignal();
				networkSignal.setNetworkType(i % 2 == 0 ? "4G" : "wifi");
				networkSignal.setRxData((long) random.nextInt(1000));
				networkSignal.setTxData((long) random.nextInt(1000));
				networkSignal.setRxSpeed((double) random.nextInt(100));
				networkSignal.setTxSpeed((double) random.nextInt(100));
				networkSignal.setTime(System.currentTimeMillis());
				networkData.getSignals().add(networkSignal);
			}

			String key = "key-" + System.currentTimeMillis();
			String value = networkData.toString();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Random data generated: " + key + ", " + value);
			}

			ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
			producer.send(record);

			try {
				Thread.sleep(INCOMING_DATA_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		producer.close();
	}

}
