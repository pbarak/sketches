package fgm.specs.common.cfg.implementation.config;

import fgm.specs.common.Vector;
import fgm.specs.common.cfg.BaseConfiguration;

public class BaseCfgImpl<T extends Vector> implements BaseConfiguration<T> {
    private static final String RECORD_SERIALIZER = "record.serializer";
    private static final String RECORD_DESERIALIZER = "record.deserializer";
    private static final String DRIFT_VECTOR_DESERIALIZER = "drift.vector.deserializer";
    private static final String DRIFT_VECTOR_SERIALIZER = "drift.vector.serializer";
    private static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
    private static final String JAVA_RMI_SERVER_HOSTNAME = "java.rmi.server.hostname";
    private static final String COORDINATOR_CLIENT_ID = "coordinator.client.id.config";
    private static final String COORDINATOR_GROUP_ID = "coordinator.group.id.config";
    private static final String COORDINATOR_APPLICATION_ID = "coordinator.application.id.config";
    private static final String INCREASE_OF_C_TOPIC = "increaseofc.messages.coordinator.topic";
    private static final String INCREASE_OF_C_TOPIC_KEY = "fgm.increaseofc.messages.topic.key";
    private static final String PHI_MESSAGES_TOPIC = "phi.messages.coordinator.topic";
    private static final String PHI_MESSAGES_TOPIC_KEY = "fgm.phimessages.topic.key";
    private static final String XI_MESSAGES_TOPIC = "xi.messages.coordinator.topic";
    private static final String XI_MESSAGES_TOPIC_KEY = "fgm.ximessages.topic.key";
    private static final String STATISTIC_MESSAGES = "statistics.messages.topic";
    private static final String SUBROUND_TOPIC_KEY = "fgm.newsubround.topic.key";
    private static final String STATISTICS_WORLDCUP_RECORDS_KEY = "statistics.worldcup.records.messages.key";
    private static final String REMOTESTORE_BIND_NAME = "remotestore.bind.name";
    private static final String REMOTESTORE_PORT = "remotestore.port";
    private static final String NUMBER_OF_NODES = "numberofnodes";
    private static final String NODE_TOPICS = "node.topics";
    private static final String NODE_TOPICS_KEY = "node.topics.key";
    private static final String LOWER_BOUND_OUTLIER = "lower.bound.outlier";
    private static final String UPPER_BOUND_OUTLIER =  "upper.bound.outlier";
    private static final String INBOUND_FILENAME = "world.cup.filename";
    private static final String FACTORY_NAME = "factory.name";
    private static final String ePSI = "algorithm.ePsi";
    private static final String WINDOW_SECONDS_WIDTH = "window.width";
    private static final String WINDOW_UPDATE_INTERVAL = "window.update.interval";
    private static final String COORDINATOR_STATE_STORE_NAME = "coordinator.state.store.name";
    FilePropertiesLocalStore filePropertiesLocalStore;
    public BaseCfgImpl(FilePropertiesLocalStore filePropertiesLocalStore) {
        if(filePropertiesLocalStore == null) {
            this.filePropertiesLocalStore = new FilePropertiesLocalStore();
        }
    }
    public BaseCfgImpl() {
        this.filePropertiesLocalStore = new FilePropertiesLocalStore();
    }

    public FilePropertiesLocalStore getFilePropertiesLocalStore( ) {
        return filePropertiesLocalStore;
    }
    public String getBootstrapServers(){
        return filePropertiesLocalStore.retrieveString( BOOTSTRAP_SERVERS );
    }
    public String getRmiHostName(){
        return filePropertiesLocalStore.retrieveString( JAVA_RMI_SERVER_HOSTNAME );
    }
    public String getCoordinatorClientId(){
        return filePropertiesLocalStore.retrieveString( COORDINATOR_CLIENT_ID );
    }
    public String getCoordinatorGroupId(){
        return filePropertiesLocalStore.retrieveString( COORDINATOR_GROUP_ID );
    }
    public String getCoordinatorApplicationId(){
        return filePropertiesLocalStore.retrieveString( COORDINATOR_APPLICATION_ID );
    }
    public String getIncreaseOfCTopic(){
        return filePropertiesLocalStore.retrieveString( INCREASE_OF_C_TOPIC );
    }
    public String getIncreaseOfCTopicKey(){
        return filePropertiesLocalStore.retrieveString( INCREASE_OF_C_TOPIC_KEY );
    }
    public String getPhiMessagesTopic(){
        return filePropertiesLocalStore.retrieveString( PHI_MESSAGES_TOPIC );
    }
    public String getPhiMessagesTopicKey(){
        return filePropertiesLocalStore.retrieveString( PHI_MESSAGES_TOPIC_KEY );
    }
    public String getXiMessagesTopic(){
        return filePropertiesLocalStore.retrieveString( XI_MESSAGES_TOPIC );
    }
    public String getXiMessagesTopicKey(){
        return filePropertiesLocalStore.retrieveString( XI_MESSAGES_TOPIC_KEY );
    }
    public String getStatisticTopic(){
        return filePropertiesLocalStore.retrieveString( STATISTIC_MESSAGES );
    }
    public String getStatisticsSubroundKey(){
        return filePropertiesLocalStore.retrieveString( SUBROUND_TOPIC_KEY );
    }
    public String getStatisticsRecordKey(){
        return filePropertiesLocalStore.retrieveString( STATISTICS_WORLDCUP_RECORDS_KEY );
    }
    public String getRemoteStoreBindName(){
        return filePropertiesLocalStore.retrieveString( REMOTESTORE_BIND_NAME );
    }
    public Integer getRemoteStorePort(){
        return filePropertiesLocalStore.retrieveInt( REMOTESTORE_PORT );
    }
    public Integer getNumberOfNodes(){
        return filePropertiesLocalStore.retrieveInt( NUMBER_OF_NODES ) ;
    }
    public String getNodesInboundTopics(){
        return filePropertiesLocalStore.retrieveString( NODE_TOPICS );
    }
    public String getNodesInboundTopicsKey(){
        return filePropertiesLocalStore.retrieveString( NODE_TOPICS_KEY );
    }
    public Double getLowerBoundOutlier(){
        return filePropertiesLocalStore.retrieveDouble( LOWER_BOUND_OUTLIER );
    }
    public Double getUpperBoundOutlier(){
        return filePropertiesLocalStore.retrieveDouble( UPPER_BOUND_OUTLIER );
    }
    public String getTopicsPopulationFileName() {
        return filePropertiesLocalStore.retrieveString( INBOUND_FILENAME );
    }
    public String getFactoryName() {
        return filePropertiesLocalStore.retrieveString( FACTORY_NAME );
    }
    public Double ePsi() {
        return filePropertiesLocalStore.retrieveDouble( ePSI );
    }
    public String getCoordinatorStateStoreName() {
        return filePropertiesLocalStore.retrieveString( COORDINATOR_STATE_STORE_NAME );
    }
    public Integer getWindowSecondsWidth() {
        return filePropertiesLocalStore.retrieveInt(WINDOW_SECONDS_WIDTH);
    }
    public Integer getWindowUpdateInterval() {
        return filePropertiesLocalStore.retrieveInt(WINDOW_UPDATE_INTERVAL);
    }
    public Class<?> getRecordSerializerClass()  {
        try {
            return Class.forName(filePropertiesLocalStore.retrieveString(RECORD_SERIALIZER));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public Class<?> getRecordDeserializerClass() {
        try {
            return Class.forName(filePropertiesLocalStore.retrieveString(RECORD_DESERIALIZER));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getDriftVectorMessageSerializerClass()  {
        try {
            return Class.forName(filePropertiesLocalStore.retrieveString(DRIFT_VECTOR_SERIALIZER));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getDriftVectorMessageDeserializerClass() {
        try {
            return Class.forName(filePropertiesLocalStore.retrieveString(DRIFT_VECTOR_DESERIALIZER));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
