package org.smartloli.kafka.game.x.book_9.sql.processor;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.game.x.book_9.sql.ignite.NetworkSignalIgniteRepository;
import org.smartloli.kafka.game.x.book_9.sql.parser.NetworkDataParser;
import org.smartloli.kafka.game.x.book_9.sql.validation.NetworkSignalValidator;

/**
 * Kafka流处理器处理JSON数据并加载到Ignite.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class NetworkDataKafkaProcessor implements Processor<byte[], byte[]> {

	public static final Logger LOG = LoggerFactory.getLogger(NetworkDataKafkaProcessor.class);

	private ProcessorContext context;

	private NetworkSignalIgniteRepository networkSignalRepository;

	private NetworkDataParser parser;

	@Override
	public void init(ProcessorContext processorContext) {
		this.context = processorContext;
		this.context.schedule(1000);

		networkSignalRepository = new NetworkSignalIgniteRepository();

		parser = new NetworkDataParser(new NetworkSignalValidator());
	}

	@Override
	public void process(byte[] key, byte[] value) {
		String json = new String(value);
		try {
			parser.parse(json).forEach(networkSignalRepository::save);
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.error("Error processing request, data: " + json, e);
			} else {
				LOG.error("Error processing request", e);
			}
		}

	}

	@Override
	public void punctuate(long timestamp) {
		context.commit();
	}

	@Override
	public void close() {
		networkSignalRepository.close();
	}

}
