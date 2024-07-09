package fgm.core.coordinator;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import fgm.specs.factory.implementation.CoordinatorDependencyInjection;
import fgm.specs.common.cfg.BaseConfiguration;
import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.serde.vectors.DriftVectorMessageDeserializer;
import fgm.specs.common.serde.vectors.DriftVectorMessageSerializer;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.collectors.increaseofc.IncreaseOfCCollectionProcessor;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.collectors.phiscollector.PhisCollectionProcessor;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.collectors.xiscollector.XisCollectionProcessor;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.statestore.CoordinatoreStateStoreBuilder;
import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.common.serde.JsonPOJODeserializer;
import fgm.specs.common.serde.JsonPOJOSerializer;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.common.messages.PhiMessage;
import fgm.specs.factory.implementation.DependencyInjection;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

public class CoordinatorApplication {
  private final static AtomicBoolean stopStreams = new AtomicBoolean(false);

  //fixed names for stream components no need to be extracted to property file, used by kafka topology declaration
  private final static String coordinatorClientId = "coordinator.client.id.config";
  private final static String coordinatorGroupId = "coordinator.group.id.config";
  private final static String factoryPropertyKey = "factory.name";
  private final static String xiMessagesSource = "xiMessages-source";
  private final static String phiMessagesSource = "phiMessages-source";
  private final static String increaseOfCMessagesSource = "increaseOfCMessages-source";
  private final static String xiMessagesProcessorName = "xiMessages-processor";
  private final static String phiMessagesProcessorName = "phiMessages-processor";
  private final static String increaseOfCMessagesProcessorName = "increaseOfCMessages-processor";
  private final static FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
  public static void main(String[] args) throws Exception {

    //__________________________________________________________________________________________________________________________________
    //a list of serdes used by the stream topology
    //the key of each message is string
    Serde<String> stringSerde = Serdes.String();
    Deserializer<String> stringDeserializer = stringSerde.deserializer();

    Map<String, Object> serdeProps = new HashMap<>();

    final Serializer<IncreaseOfCMessage> increaseOfCMessageSerializer = new JsonPOJOSerializer<>();
    serdeProps.put("JsonPOJOClass", IncreaseOfCMessage.class);
    increaseOfCMessageSerializer.configure(serdeProps, false);

    final Deserializer<IncreaseOfCMessage> increaseOfCMessageDeserializer = new JsonPOJODeserializer<>();
    serdeProps.put("JsonPOJOClass", IncreaseOfCMessage.class);
    increaseOfCMessageDeserializer.configure(serdeProps, false);

    final Serde<IncreaseOfCMessage> increaseOfCMessageSerde = Serdes.serdeFrom(increaseOfCMessageSerializer, increaseOfCMessageDeserializer);

//    final Serializer<DriftVectorMessage> stateMessageSerializer = new JsonPOJOSerializer<>();
//    serdeProps.put("JsonPOJOClass", DriftVectorMessage.class);
//    stateMessageSerializer.configure(serdeProps, false);
//
//    final Deserializer<DriftVectorMessage> stateMessageDeserializer = new JsonPOJODeserializer<>();
//    serdeProps.put("JsonPOJOClass", DriftVectorMessage.class);
//    stateMessageDeserializer.configure(serdeProps, false);

    final Serializer<DriftVectorMessage> stateMessageSerializer = new DriftVectorMessageSerializer();
    serdeProps.put("JsonPOJOClass", DriftVectorMessage.class);
    stateMessageSerializer.configure(serdeProps, false);

    final Deserializer<DriftVectorMessage> stateMessageDeserializer = new DriftVectorMessageDeserializer();
    serdeProps.put("JsonPOJOClass", DriftVectorMessage.class);
    stateMessageDeserializer.configure(serdeProps, false);

    final Serde<DriftVectorMessage> stateMessageSerde = Serdes.serdeFrom(stateMessageSerializer, stateMessageDeserializer);

    final Serializer<PhiMessage> phiMessageSerializer = new JsonPOJOSerializer<>();
    serdeProps.put("JsonPOJOClass", PhiMessage.class);
    phiMessageSerializer.configure(serdeProps, false);

    final Deserializer<PhiMessage> phiMessageDeserializer = new JsonPOJODeserializer<>();
    serdeProps.put("JsonPOJOClass", PhiMessage.class);
    phiMessageDeserializer.configure(serdeProps, false);

    final Serde<PhiMessage> phiMessageSerde = Serdes.serdeFrom(phiMessageSerializer, phiMessageDeserializer);
    //____________________________________________________________________________________________________________________________

    CoordinatoreStateStoreBuilder<?> customStoreBuilder = CoordinatorDependencyInjection.init(filePropertiesLocalStore.retrieveString(factoryPropertyKey));
    BaseConfiguration<?> baseConfiguration = CoordinatorDependencyInjection.createFactory(filePropertiesLocalStore.retrieveString(factoryPropertyKey)).getBaseCfg();

    Topology topology = new Topology();

    String remoteStoreUrl = "rmi://" + baseConfiguration.getRmiHostName() +
            ":" + baseConfiguration.getRemoteStorePort() +
            "/" + baseConfiguration.getRemoteStoreBindName();


    topology
        .addSource(increaseOfCMessagesSource, stringDeserializer, increaseOfCMessageDeserializer, baseConfiguration.getIncreaseOfCTopic())
        .addSource(xiMessagesSource, stringDeserializer, stateMessageDeserializer, baseConfiguration.getXiMessagesTopic())
        .addSource(phiMessagesSource, stringDeserializer, phiMessageDeserializer, baseConfiguration.getPhiMessagesTopic())
            .addProcessor(increaseOfCMessagesProcessorName, () -> {
              try {
                return new IncreaseOfCCollectionProcessor(customStoreBuilder.name(), remoteStoreUrl, DependencyInjection.getFactory().getBaseCfg());
              } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
              } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
              } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
              } catch (InstantiationException e) {
                throw new RuntimeException(e);
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              }
            }, increaseOfCMessagesSource)
            .addProcessor(xiMessagesProcessorName, () -> {
              try {
                return new XisCollectionProcessor(customStoreBuilder.name(), remoteStoreUrl, DependencyInjection.getFactory());
              } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
              } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
              } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
              } catch (InstantiationException e) {
                throw new RuntimeException(e);
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              }
            }, xiMessagesSource)
            .addProcessor(phiMessagesProcessorName, () -> new PhisCollectionProcessor(customStoreBuilder.name(), remoteStoreUrl), phiMessagesSource)
            .addStateStore(customStoreBuilder, increaseOfCMessagesProcessorName, xiMessagesProcessorName, phiMessagesProcessorName);

    Properties props = new Properties();
    props.put(StreamsConfig.CLIENT_ID_CONFIG, coordinatorClientId);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, coordinatorGroupId);
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, baseConfiguration.getCoordinatorApplicationId());
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, baseConfiguration.getBootstrapServers());
    //props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 2);

////    props.put(StreamsConfig.consumerPrefix(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG), "earliest");
////    //props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
////    //props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
////    //  props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
////    // WallclockTimestampExtractor.class);
////            props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
////    props.put("max.poll.interval.ms", 600000);
////        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
////    props.put("max.poll.records", 1);
////        // The magic. Set the stream to use exactly-once semantics rather than at-least-once..
////        ////props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
    KafkaStreams kafkaStreams = new KafkaStreams(topology, props);
    props.put(StreamsConfig.consumerPrefix(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG), "earliest");
    //props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    //props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
    //  props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
    // WallclockTimestampExtractor.class);
    //props.put("cache.max.bytes.buffering", 0);
    //props.put("processing.guarantee", "exactly_once");

    props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
    //props.put("commit.interval.ms", 100);
    props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
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
