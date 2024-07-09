package fgm.specs.site.commands;

import fgm.specs.factory.implementation.Factory;
import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.messages.PhiMessage;
import fgm.specs.site.state.implementation.state.FgmNodeImpl;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.function.FgmCommand;
import fgm.specs.site.state.LocalStatistic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.DateTime;

public class SendPhiMessageCommand implements FgmCommand {
    private PhiMessage phiMessage;
    private int currentRound;
    private int currentSubRound;
    private FgmNodeImpl<?> fgmNode;
    static Logger logger = Logger.getLogger(SendPhiMessageCommand.class.getName());
    FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
    public SendPhiMessageCommand() {
    }

    public SendPhiMessageCommand(FgmNodeImpl<?> fgmNode) {
        this.fgmNode = fgmNode;
        this.currentRound = fgmNode.getCurrentRound();
        this.currentSubRound = fgmNode.getCurrentSubRound();
        PropertyConfigurator.configure(getClass().getClassLoader().getResourceAsStream("log4j.properties"));
    }
    @Override
    public void execute() {
        try{
        this.phiMessage = Factory.getPhiMessage();
        this.phiMessage.setSiteId(this.fgmNode.getNodeId());
        this.phiMessage.setCurrentRound(this.currentRound);
        this.phiMessage.setCurrentSubround(this.currentSubRound);
        logger.info("in SendPhiMessageCommand of " + this.fgmNode.getNodeId() + " requesting getXi() at " + DateTime.now().getMillis());
        LocalStatistic<? extends Vector> localXi = this.fgmNode.getXi();
        GlobalStatistic<? extends Vector> localE = this.fgmNode.getE();

        Double result = this.fgmNode.getSafeFunction().computePhi(
                localXi.getVector(),
                localE.getVector(),
                this.fgmNode.getT());

        this.phiMessage.setPhi(result);


        ////this.stateMessageBeanWindowed.getPhiMessagesProducer().beginTransaction();
        this.fgmNode.getPhiMessagesProducer().send(
                new ProducerRecord <>(filePropertiesLocalStore.retrieveString("phi.messages.coordinator.topic"), filePropertiesLocalStore.retrieveString("fgm.phimessages.topic.key"), this.phiMessage));
        //this.fgmNode.getStatisticsMessageProducer().send(new ProducerRecord<>(filePropertiesLocalStore.retrieveString("statistics.messages.topic"), filePropertiesLocalStore.retrieveString("fgm.phimessages.topic.key"), StatisticsMessage.getInstance(this.fgmNode.getCurrentRound(), this.fgmNode.getCurrentSubRound(), this.fgmNode.getNodeId(), MessageType.phiMessage.getMessageType())));
        //RecordMetadata recordMetadata = response.get();
        //producer.flush();
        ////this.stateMessageBeanWindowed.getPhiMessagesProducer().commitTransaction();

        } catch (Exception ex) {
            ////this.stateMessageBeanWindowed.getPhiMessagesProducer().abortTransaction();
            logger.info(ex.getMessage());
        }
    }
}
