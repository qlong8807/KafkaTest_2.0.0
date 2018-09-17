package com.zz.test.kafka.stream;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

public class StreamWordCountTest {

	public static void main(String[] args) {

		Properties config = new Properties();
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application1");
		config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "cos93:9092");
		config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		StreamsBuilder builder = new StreamsBuilder();

		// Serializers/deserializers (serde) for String and Long types
		final Serde<String> stringSerde = Serdes.String();
		final Serde<Long> longSerde = Serdes.Long();

		// 指定读取的key,value都是String的
		KStream<String, String> source = builder.stream("streams-text-input",
				Consumed.with(stringSerde, stringSerde));

		 KTable<String, Long> wordCounts = source
		 // Split each text line, by whitespace, into words.
		 .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
		 // Group the text words as message keys
		 .groupBy((key, value) -> value)
		 // Count the occurrences of each word (message key).
		 .count();
		// 上面的lambda表达式等同于下面的
//		KTable<String, Long> wordCounts = source.flatMapValues(new ValueMapper<String, Iterable<String>>() {
//			@Override
//			public Iterable<String> apply(String value) {
//				System.out.println("--------"+value);
//				return Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+"));
//			}
//		}).groupBy(new KeyValueMapper<String, String, String>() {
//			@Override
//			public String apply(String key, String value) {
//				System.out.println(key+"----------"+value);
//				return value;
//			}
//		}).count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
		// Materialize the result into a KeyValueStore named "counts-store".
		// The Materialized store is always of type <Bytes, byte[]> as this is the
		// format of the inner most store.
		 
		// Store the running counts as a changelog stream to the output topic.
		// 指定保存的key,value是String,Long型的。
		wordCounts.toStream().to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));
		// System.err.println(wordCounts.);
		KafkaStreams streams = new KafkaStreams(builder.build(), config);

		streams.start();
	}

}
