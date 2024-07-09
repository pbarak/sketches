package fgm.specs.site.commands;

import fgm.specs.factory.implementation.Factory;
import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.site.state.implementation.state.FgmNodeImpl;
import fgm.specs.function.FgmCommand;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
public class SendXiMessageCommand implements FgmCommand {
  private String siteId;
  private DriftVectorMessage stateMessage;
  private FgmNodeImpl<?> fgmNode;
  static Logger logger = Logger.getLogger(SendXiMessageCommand.class.getName());
  FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
  public SendXiMessageCommand() {
  }

  public SendXiMessageCommand(FgmNodeImpl<?> fgmNode) {
    this.fgmNode = fgmNode;
    this.fgmNode.getNodeInternalFgmState().setCurrentSubRound(0);
    this.fgmNode.getNodeInternalFgmState().setCurrentRound(this.fgmNode.getCurrentRound() + 1);
    this.siteId = this.fgmNode.getNodeId();
  }

  @Override
  public void execute() {
    try{
      logger.info(this.siteId);
      this.stateMessage = Factory.getStateMessage();
      this.stateMessage.setSiteId(this.siteId);
      this.stateMessage.setStateVector(this.fgmNode.getXi().getVector());
      this.stateMessage.setCurrentRound(this.fgmNode.getCurrentRound());
      this.stateMessage.setCurrentSubround(this.fgmNode.getCurrentSubRound());

      //this.stateMessageBeanWindowed.getXiMessagesProducer().beginTransaction();
      this.fgmNode.getXiMessagesProducer().send(new ProducerRecord<>(filePropertiesLocalStore.retrieveString("xi.messages.coordinator.topic"), filePropertiesLocalStore.retrieveString("fgm.ximessages.topic.key"), this.stateMessage));
      //this.fgmNode.getStatisticsMessageProducer().send(new ProducerRecord<>(filePropertiesLocalStore.retrieveString("statistics.messages.topic"), filePropertiesLocalStore.retrieveString("fgm.ximessages.topic.key"), StatisticsMessage.getInstance(this.fgmNode.getCurrentRound(), this.fgmNode.getCurrentSubRound(), this.fgmNode.getNodeId(), MessageType.xiMessage.getMessageType())));
      //RecordMetadata recordMetadata = response.get();
      //producer.flush();
      //this.stateMessageBeanWindowed.getXiMessagesProducer().commitTransaction();
    } catch (Exception ex) {
      ////this.stateMessageBeanWindowed.getXiMessagesProducer().abortTransaction();
      logger.info(ex.toString());
    }
  }
}



