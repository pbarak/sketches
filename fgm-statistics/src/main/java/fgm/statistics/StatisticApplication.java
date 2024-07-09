package fgm.statistics;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class StatisticApplication {
  private final static AtomicBoolean stopStreams = new AtomicBoolean(false);
  public static void main(String[] args) throws Exception {

    Serde<String> stringSerde = Serdes.String();
    Serde<byte[]> byteArraySerde = Serdes.ByteArray();

    Deserializer<String> stringDeserializer = stringSerde.deserializer();
    Deserializer<byte[]> byteArrayDeserializer2 = byteArraySerde.deserializer();

    Topology topology = new Topology();

    String vectorSourceNodeName = args[1];
    String vectorProcessorName = args[2];
    String stateStoreName = args[3];

    topology.addSource(vectorSourceNodeName, stringDeserializer, byteArrayDeserializer2, args[0])
      .addProcessor(vectorProcessorName, ()->new StatisticsProcessor(), vectorSourceNodeName);


    Properties props = new Properties();
    props.put(StreamsConfig.CLIENT_ID_CONFIG, args[4]);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, args[5]);
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, args[6]);
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "clu02.softnet.tuc.gr:6667,clu03.softnet.tuc.gr:6667,clu04.softnet.tuc.gr:6667,clu06.softnet.tuc.gr:6667");
    //props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
    props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/pbarakos/state");
    //props.put(StreamsConfig.consumerPrefix(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG), "earliest");
    //props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    //props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
    //props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);

    props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);

    props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    // The magic. Set the stream to use exactly-once semantics rather than at-least-once..
    props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

    KafkaStreams kafkaStreams = new KafkaStreams(topology, props);
    kafkaStreams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
    //here you should examine the throwable/exception and perform an appropriate action!
      kafkaStreams.close(1000L, TimeUnit.MILLISECONDS);
    });

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    kafkaStreams.setUncaughtExceptionHandler((t, e) -> {
      stopStreams.set(true);
    });

    System.out.println("Starting FGM Application now");
    kafkaStreams.cleanUp();
    kafkaStreams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      stopStreams.set(true);
    }));

    while (!stopStreams.get()) {
      Thread.sleep(1000);
    }

    kafkaStreams.close();
    }
}
