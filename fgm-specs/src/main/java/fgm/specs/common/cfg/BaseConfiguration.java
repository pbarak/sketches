package fgm.specs.common.cfg;

import fgm.specs.common.Vector;

public interface BaseConfiguration <T extends Vector> {

        String getBootstrapServers();
        String getRmiHostName();
        String getCoordinatorClientId();
        String getCoordinatorGroupId();
        String getCoordinatorApplicationId();
        String getIncreaseOfCTopic();
        String getIncreaseOfCTopicKey();
        String getPhiMessagesTopic();
        String getPhiMessagesTopicKey();
        String getXiMessagesTopic();
        String getXiMessagesTopicKey();
        String getStatisticTopic();
        String getStatisticsSubroundKey();
        String getStatisticsRecordKey();
        String getRemoteStoreBindName();
        Integer getRemoteStorePort();
        Integer getNumberOfNodes();
        String getNodesInboundTopics();
        String getNodesInboundTopicsKey();
        Double getLowerBoundOutlier();
        Double getUpperBoundOutlier();
        String getTopicsPopulationFileName();
        String getFactoryName();
        Double ePsi();
        String getCoordinatorStateStoreName();
        Integer getWindowSecondsWidth();
        Integer getWindowUpdateInterval();
        Class<?> getRecordSerializerClass() throws ClassNotFoundException;
        Class<?> getRecordDeserializerClass() throws ClassNotFoundException;
        Class<?> getDriftVectorMessageSerializerClass() throws ClassNotFoundException;
        Class<?> getDriftVectorMessageDeserializerClass() throws ClassNotFoundException;

}
