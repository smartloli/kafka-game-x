package org.smartloli.kafka.game.x.book_9.sql;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.game.x.book_9.sql.processor.NetworkDataKafkaProcessor;
import org.smartloli.kafka.game.x.book_9.sql.util.ConfigUtil;

import java.util.Properties;

/**
 * 使用线程对数据进行逻辑处理.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class NetworkDataProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkDataProcessor.class);

    private static Processor<byte[], byte[]> getProcessor() {
        return new NetworkDataKafkaProcessor();
    }

    @Override
    public void run() {
        LOG.info("Initializing kafka processor...");

        Properties properties = ConfigUtil.getConfig("network-data");
        String topics = properties.getProperty("topic.names");
        StreamsConfig config = new StreamsConfig(properties);

        LOG.info("Start listening topics: " + topics);

        TopologyBuilder builder = new TopologyBuilder().addSource("SOURCE", topics.split(","))
                                                       .addProcessor("PROCESSOR", NetworkDataProcessor::getProcessor, "SOURCE");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();
    }
}
