package fgm.specs.factory.implementation;

import fgm.specs.common.cfg.BaseConfiguration;
import fgm.specs.common.cfg.implementation.config.BaseCfgImpl;
import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.common.messages.PhiMessage;
import fgm.specs.data.StreamRecord;
import fgm.specs.data.WindowRecord;
import fgm.specs.function.implementation.queryfunction.KeyFrequencyListQFunction;
import fgm.specs.function.implementation.safefunction.NormSafeFunction;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.state.FgmGlobalStatistic;
import fgm.specs.common.messages.StatisticsMessage;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.common.state.FgmNodeState;
import fgm.specs.common.vector.KeyFrequencyList;
import fgm.specs.factory.IFgmFactory;
import fgm.specs.function.QueryFunction;
import fgm.specs.function.SafeFunction;
import fgm.specs.site.convertor.IToStateVectorConvertor;
import fgm.specs.site.convertor.implementation.ToStateRecord;
import fgm.specs.site.state.LocalStatistic;
import fgm.specs.site.convertor.IToWindowRecordConvertor;
import fgm.specs.site.state.implementation.state.FgmLocalStatistic;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
public class KeyFrequencyListFactory<T extends Vector> implements IFgmFactory<KeyFrequencyList> {
    private static FilePropertiesLocalStore filePropertiesLocalStore;

    private static Producer<String, DriftVectorMessage> xiMessageProducer;
    private static Producer<String, IncreaseOfCMessage> increaseOfMessageCproducer;
    private static Producer<String, PhiMessage> phiMessageproducer;
    private final static String stateStoreName = "StateStore";

    public KeyFrequencyListFactory() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();

    }

    public String getCoordinatorStateStoreName () {
        return stateStoreName;
    }

    public LocalStatistic<KeyFrequencyList> getLocalStatistic(){
        try {
            KeyFrequencyList keyFrequencyList = new KeyFrequencyList(DependencyInjection.getFactory());
            return new FgmLocalStatistic<>(keyFrequencyList);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public GlobalStatistic<KeyFrequencyList> getGlobalStatistic() {
        try {
            return new FgmGlobalStatistic<>(new KeyFrequencyList(DependencyInjection.getFactory()));
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
    }
    @Override
    public QueryFunction<KeyFrequencyList> getQueryFunction() {
        return new KeyFrequencyListQFunction();
    }

    public SafeFunction<KeyFrequencyList> getSafeFunction() {
        return  new NormSafeFunction();
    }

    public DriftVectorMessage getStateMessage() {
        return new DriftVectorMessage();
    }

    public PhiMessage getPhiMessage() {
        return new PhiMessage();
    }

    public IncreaseOfCMessage getIncreaseOfCMessage() {return new IncreaseOfCMessage();}

    public Producer<String, DriftVectorMessage> getXiMessageProducer(){
        return createXiMessagesProducer();
    }

    public Producer<String, PhiMessage> getPhiMessageProducer(){
        return createPhiMessagesProducer();
    }

    public Producer<String, IncreaseOfCMessage> getIncreaseOfCMessageProducer(){
        return createIncreaseOfCMessagesProducer();
    }

    @Override
    public BaseConfiguration<KeyFrequencyList> getBaseCfg() {
        return new BaseCfgImpl<>();
    }

    @Override
    public <IMPL0 extends WindowRecord, IMPL2 extends StreamRecord> IToWindowRecordConvertor<IMPL0, IMPL2> getToWindowRecordConvertor() {
        return null;
    }

    @Override
    public <K extends Long> IToStateVectorConvertor<K> toStateVectorRecord(){
        return new ToStateRecord<>();
    }


    private Producer<String, StatisticsMessage> createStatisticsMessagesProducer() {

        Properties properties = new Properties();

        properties.put("bootstrap.servers", filePropertiesLocalStore.retrieveProperty("bootstrap.servers"));
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

        properties.put("bootstrap.servers", filePropertiesLocalStore.retrieveProperty("bootstrap.servers"));
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

        properties.put("bootstrap.servers", filePropertiesLocalStore.retrieveProperty("bootstrap.servers"));
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

    public Producer<String, IncreaseOfCMessage> createIncreaseOfCMessagesProducer() {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", filePropertiesLocalStore.retrieveProperty("bootstrap.servers"));
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

    public FgmNodeState<KeyFrequencyList> getFgmNodeState(){
        return new FgmNodeState<>(this);
    }


}

