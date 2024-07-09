package fgm.specs.factory.implementation;

import fgm.specs.common.cfg.BaseConfiguration;
import fgm.specs.common.cfg.implementation.config.BaseCfgImpl;
import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.common.messages.PhiMessage;
import fgm.specs.data.implementation.stream.WorldCupRecord;
import fgm.specs.function.implementation.queryfunction.GenericKeyFrequencyListQFunction;
import fgm.specs.function.implementation.safefunction.GenericNormSafeFunction;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.state.FgmGlobalStatistic;
import fgm.specs.common.messages.StatisticsMessage;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.common.state.FgmNodeState;
import fgm.specs.common.vector.GenericKeyFrequencyList;
import fgm.specs.factory.IFgmFactory;
import fgm.specs.function.QueryFunction;
import fgm.specs.function.SafeFunction;
import fgm.specs.site.convertor.IToStateVectorConvertor;
import fgm.specs.site.state.LocalStatistic;
import fgm.specs.site.convertor.implementation.ToWindowRecordConvertor;
import fgm.specs.site.state.implementation.state.FgmLocalStatistic;
import fgm.specs.site.state.implementation.state.WindowWorldCupRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class GenericKeyFrequencyListFactory<T extends Vector> implements IFgmFactory<GenericKeyFrequencyList<Integer>> {
    private static Producer<String, byte[]> producer;
    private static Producer<String, DriftVectorMessage> xiMessageProducer;
    private static Producer<String, IncreaseOfCMessage> increaseOfMessageCproducer;
    private static Producer<String, PhiMessage> phiMessageproducer;
    private final static String stateStoreName = "StateStore";

    public GenericKeyFrequencyListFactory() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    }

    public String getCoordinatorStateStoreName () {
        return stateStoreName;
    }

    public LocalStatistic<GenericKeyFrequencyList<Integer>> getLocalStatistic(){
        try {
            GenericKeyFrequencyList<Integer> genericKeyFrequencyList = new GenericKeyFrequencyList<Integer>();
            return new FgmLocalStatistic<>(genericKeyFrequencyList);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public GlobalStatistic<GenericKeyFrequencyList<Integer>> getGlobalStatistic(){
        return new FgmGlobalStatistic<>(new GenericKeyFrequencyList<>());
    }

    @Override
    public QueryFunction<GenericKeyFrequencyList<Integer>> getQueryFunction() {
        return new GenericKeyFrequencyListQFunction();
    }
    public SafeFunction<GenericKeyFrequencyList<Integer>> getSafeFunction() {
        return  new GenericNormSafeFunction();
    }

    public DriftVectorMessage getStateMessage() {
        return new DriftVectorMessage();
    }

    public PhiMessage getPhiMessage() {
        return new PhiMessage();
    }

    public IncreaseOfCMessage getIncreaseOfCMessage() {return new IncreaseOfCMessage();}

    public Producer<String, byte[]> getProducer(){
        return createProducer();
    }

    public Producer<String, DriftVectorMessage> getXiMessageProducer(){
        return createXiMessagesProducer();
    }

    public Producer<String, PhiMessage> getPhiMessageProducer(){
        return createPhiMessagesProducer();
    }

    public Producer<String, IncreaseOfCMessage> getIncreaseOfCMessageProducer(){
        return createIncreaseOfCMessagesProducer();
    }
    public ToWindowRecordConvertor<WindowWorldCupRecord, WorldCupRecord> getToWindowRecordConvertor()  {
        return new ToWindowRecordConvertor<>();
    }

    @Override
    public <K extends Long> IToStateVectorConvertor<K> toStateVectorRecord(){
        return null;
    }

    private Producer<String, byte[]> createProducer() {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", this.getBaseCfg().getBootstrapServers());
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put("enable.auto.commit", "true");
        //properties.put("retries", 3);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1048576);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);

        // transaction properties.
        //// properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString().replace("-", "")); // unique transactional id.
        //// properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        //// properties.put(ProducerConfig.ACKS_CONFIG, "all");
        //// properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //// properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        //// properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 600000);

        Producer<String, byte[]> producer = new KafkaProducer<>(properties);
        Callback callback = (metadata, exception)->{
            if (exception != null) {
                exception.printStackTrace();
            }
        };
        ////producer.initTransactions();
        return producer;
    }


    private Producer<String, StatisticsMessage> createStatisticsMessagesProducer() {

        Properties properties = new Properties();

        properties.put("bootstrap.servers", this.getBaseCfg().getBootstrapServers());
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "fgm.specs.common.serde.JsonPOJOSerializer");
        properties.put("enable.auto.commit", "true");
        //properties.put("retries", 3);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1048576);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);

        // transaction properties.
        //// properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString().replace("-", "")); // unique transactional id.
        //// properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        //// properties.put(ProducerConfig.ACKS_CONFIG, "all");
        //// properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //// properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        //// properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 600000);

        Producer<String, StatisticsMessage> producer = new KafkaProducer<>(properties);
        Callback callback = (metadata, exception)->{
            if (exception != null) {
                exception.printStackTrace();
            }
        };
        ////producer.initTransactions();
        return producer;
    }

    private Producer<String, DriftVectorMessage> createXiMessagesProducer() {

        Properties properties = new Properties();

        properties.put("bootstrap.servers", this.getBaseCfg().getBootstrapServers());
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "fgm.specs.common.serde.vectors.DriftVectorMessageSerializer");
        properties.put("enable.auto.commit", "true");
        //properties.put("retries", 3);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1048576);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);

        // transaction properties.
        //// properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString().replace("-", "")); // unique transactional id.
        //// properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        //// properties.put(ProducerConfig.ACKS_CONFIG, "all");
        //// properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //// properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        //// properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 600000);

        Producer<String, DriftVectorMessage> producer = new KafkaProducer<>(properties);
        Callback callback = (metadata, exception)->{
            if (exception != null) {
                exception.printStackTrace();
            }
        };
        ////producer.initTransactions();
        return producer;
    }
    private Producer<String, PhiMessage> createPhiMessagesProducer() {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", this.getBaseCfg().getBootstrapServers());
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "fgm.specs.common.serde.JsonPOJOSerializer");
        properties.put("enable.auto.commit", "true");
        //properties.put("retries", 3);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1048576);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);

        // transaction properties.
        //// properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString().replace("-", "")); // unique transactional id.
        //// properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        //// properties.put(ProducerConfig.ACKS_CONFIG, "all");
        //// properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //// properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        //// properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 600000);

        Producer<String, PhiMessage> producer = new KafkaProducer<>(properties);
        Callback callback = (metadata, exception)->{
            if (exception != null) {
                exception.printStackTrace();
            }
        };
        ////producer.initTransactions();
        return producer;

    }

    private Producer<String, IncreaseOfCMessage> createIncreaseOfCMessagesProducer() {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", this.getBaseCfg().getBootstrapServers());
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "fgm.specs.common.serde.JsonPOJOSerializer");
        properties.put("enable.auto.commit", "true");
        //properties.put("retries", 3);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1048576);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);

        // transaction properties.
        //// properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString().replace("-", "")); // unique transactional id.
        //// properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        //// properties.put(ProducerConfig.ACKS_CONFIG, "all");
        //// properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //// properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        //// properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 600000);

        Producer<String, IncreaseOfCMessage> producer = new KafkaProducer<>(properties);
        Callback callback = (metadata, exception)->{
            if (exception != null) {
                exception.printStackTrace();
            }
        };
        ////producer.initTransactions();
        return producer;
    }

    public FgmNodeState<GenericKeyFrequencyList<Integer>> getFgmNodeState(){
        return new FgmNodeState<>(this);
    }

    @Override
    public BaseConfiguration<GenericKeyFrequencyList<Integer>> getBaseCfg() {
        return new BaseCfgImpl<>();
    }

}


