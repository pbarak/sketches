package fgm.core.site;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import fgm.specs.common.cfg.BaseConfiguration;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.common.serde.JsonPOJOSerializer;
import fgm.specs.common.serde.records.RecordDeserializer;
import fgm.specs.common.serde.records.RecordSerializer;
import fgm.specs.data.StreamRecord;
import fgm.specs.factory.IFgmFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class SiteApplication {
    private final static AtomicBoolean stopStreams = new AtomicBoolean(false);
    private final static String sinkName = "SiteApplicationSinkName";
    public static void main(String[] args) throws Exception {

        SiteProcessor siteProcessor = SiteProcessor.newInstance();

        IFgmFactory<?> factory = SiteProcessor.getFactory();

        BaseConfiguration<?> baseConfiguration = factory.getBaseCfg();

        Serde<String> stringSerde = Serdes.String();
        Deserializer<String> stringDeserializer = stringSerde.deserializer();

        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<?> increaseOfCMessageSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", IncreaseOfCMessage.class);
        increaseOfCMessageSerializer.configure(serdeProps, false);

        final Serializer<StreamRecord> recordSerializer = new RecordSerializer();
        serdeProps.put("JsonPOJOClass", baseConfiguration.getRecordSerializerClass());
        recordSerializer.configure(serdeProps, false);

        final Deserializer<?> recordDeserializer = new RecordDeserializer();
        serdeProps.put("JsonPOJOClass", baseConfiguration.getRecordDeserializerClass());
        recordDeserializer.configure(serdeProps, false);


        Topology topology = new Topology();
        String vectorSink1 = args[1];
        String vectorSink2 = args[2];
        String vectorSourceNodeName = args[3];
        String vectorProcessorName = args[4];
        String firstStateStoreName = args[5];

        topology.addSource(vectorSourceNodeName, stringDeserializer, recordDeserializer, args[0])
                .addProcessor(vectorProcessorName, () -> siteProcessor, vectorSourceNodeName)
                .addSink(sinkName, baseConfiguration.getIncreaseOfCTopic(), stringSerde.serializer(), increaseOfCMessageSerializer, vectorProcessorName);
        //.addStateStore(vectorStoreBuilder, vectorProcessorName);

        String nodeId = UUID.randomUUID().toString().replace("-", "");

        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, nodeId);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, args[7]);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, nodeId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, baseConfiguration.getBootstrapServers());
        //props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        //props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/pbarakos/state");
        props.put(StreamsConfig.consumerPrefix(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG), "earliest");

        //props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        //props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        //props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
        //props.put("max.poll.interval.ms", 600000);
        //props.put("max.poll.records", 10);
        //props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        // The magic. Set the stream to use exactly-once semantics rather than at-least-once..
        //props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);
        kafkaStreams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
            // here you should examine the throwable/exception and perform an appropriate action!
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
