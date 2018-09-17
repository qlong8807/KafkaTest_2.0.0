package com.zz.test.kafka.stream;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

/**
 * @author zyl
 * @date 2018年8月8日
 * @desc 从一个topic把内存按单词拆分后传输到另一个topic
 */
public class StreamLineSplitTest {
	public static void main(String[] args) {
		Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-linesplit");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "cos93:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String,String> source = builder.stream("streams-text-input");
        source.flatMapValues(value -> Arrays.asList(value.split("\\W+"))).to("streams-linesplit-output");
        Topology topology = builder.build();
        System.out.println(topology.describe());
        KafkaStreams streams = new KafkaStreams(topology, config);
        //最好添加一个钩子，在程序关闭的时候关闭stream
        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
			@Override
			public void run() {
				System.out.println("hook closing");
				streams.close();
				latch.countDown();
			}
        });
        try {
	        	streams.start();
			latch.await();
		} catch (InterruptedException e) {
			System.exit(1);
		}
        System.exit(0);
	}
}
