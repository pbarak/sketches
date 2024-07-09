package fgm.specs.factory.implementation;

import fgm.specs.common.cfg.FastAGMSConfiguration;
import fgm.specs.common.cfg.implementation.config.FastAGMSCfgImpl;
import fgm.specs.site.convertor.IToStateVectorConvertor;
import fgm.specs.site.convertor.IToWindowRecordConvertor;
import fgm.specs.site.convertor.implementation.ToStateRecord;
import fgm.specs.site.convertor.implementation.ToWindowRecordConvertor;
import fgm.specs.site.state.implementation.state.CustomSlidingWindow;
import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.common.messages.PhiMessage;
import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.function.implementation.queryfunction.FastAGMSQueryFunction;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.state.FgmGlobalStatistic;
import fgm.specs.common.messages.StatisticsMessage;
import fgm.specs.function.implementation.safefunction.SelfJoin;
import fgm.specs.common.state.FgmNodeState;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.common.vector.FastAGMS;
import fgm.specs.factory.IFgmFactory;
import fgm.specs.function.QueryFunction;
import fgm.specs.function.SafeFunction;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import fgm.specs.site.state.LocalStatistic;
import fgm.specs.site.state.implementation.state.FgmLocalStatistic;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;

public class Factory<T extends Vector> implements IFgmFactory<FastAGMS> {
    private static FilePropertiesLocalStore filePropertiesLocalStore;
    private static Producer<String, DriftVectorMessage> xiMessageProducer;
    private static Producer<String, IncreaseOfCMessage> increaseOfMessageCproducer;
    private static Producer<String, PhiMessage> phiMessageproducer;
    public Factory() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
    }


//    public static CentralizedFastAGMS getStatistic(){ //todo check if class implements interface ?
//           }return new CentralizedFastAGMS(7, 5000);
//
    public FgmNodeState<FastAGMS> getFgmNodeState(){
        return new FgmNodeState<>(this);
    }

    public LocalStatistic<FastAGMS> getLocalStatistic(){
        try {
//      System.out.println("the depth in context is" + FGMContextFactory.getInstance(clazz).getProps().getProperty("depth"));
//      return  clazz
//          .getConstructor(clazz)
//          .newInstance(FGMContextFactory.getInstance(clazz));

            FastAGMS fastAGMS = new FastAGMS(this.getBaseCfg().getSketchDepth(), this.getBaseCfg().getSketchBuckets(), DependencyInjection.getFactory().toStateVectorRecord());
            return new FgmLocalStatistic<>(fastAGMS);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public FastAGMSConfiguration<FastAGMS> getBaseCfg() {
        return new FastAGMSCfgImpl<>();
    }

    public CustomSlidingWindow<FastAGMS> getCustomSlidingWindow(){
        return new CustomSlidingWindow<>(this);
    }

    public GlobalStatistic<FastAGMS> getGlobalStatistic(){
        return new FgmGlobalStatistic<>(new FastAGMS());
    }

    @Override
    public QueryFunction<FastAGMS> getQueryFunction() {
        QueryFunction<FastAGMS> result =  new FastAGMSQueryFunction();
        return result;
    }
    public SafeFunction<FastAGMS> getSafeFunction() {
        return  new SelfJoin();
    }

    public static DriftVectorMessage getStateMessage() {
        return new DriftVectorMessage();
    }

    public static PhiMessage getPhiMessage() {
        return new PhiMessage();
    }

    public static IncreaseOfCMessage getIncreaseOfCMessage() {return new IncreaseOfCMessage();}

    //  public static StreamRecord deserialize(byte[] record) {
    //    return (WorldCupRecord) SerializationUtils.deserialize(record);
    //  }
    public Producer<String, DriftVectorMessage> getXiMessageProducer(){
        return createXiMessagesProducer();
    }

    public Producer<String, PhiMessage> getPhiMessageProducer(){
        return createPhiMessagesProducer();
    }

    public Producer<String, IncreaseOfCMessage> getIncreaseOfCMessageProducer(){
        return createIncreaseOfCMessagesProducer();
    }

    public IToWindowRecordConvertor<?, ?> getToWindowRecordConvertor()  {
        return (new ToWindowRecordConvertor<>());
    }

    public IToStateVectorConvertor<?> toStateVectorRecord() {
            return new ToStateRecord<>();
    }
        private Producer<String, StatisticsMessage> createStatisticsMessagesProducer() {

        Properties properties = new Properties();

        properties.put("bootstrap.servers", filePropertiesLocalStore.retrieveProperty("bootstrap.servers"));
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "fgm.specs.common.serde.JsonPOJOSerializer");
        properties.put("enable.auto.commit", "true");
        ////properties.put("retries", 3);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1048576);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);

        //// transaction properties.
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

    private Producer<String, IncreaseOfCMessage> createIncreaseOfCMessagesProducer() {
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
}

