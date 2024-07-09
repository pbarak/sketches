package fgm.specs.site.commands;

import fgm.specs.factory.implementation.Factory;
import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.site.state.implementation.state.FgmNodeImpl;
import fgm.specs.data.implementation.stream.WorldCupRecord;
import fgm.specs.data.StreamRecord;
import fgm.specs.function.FgmCommand;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;


public class SendIncreaseOfCCommand implements FgmCommand{
  static Logger logger = Logger.getLogger(SendIncreaseOfCCommand.class.getName());
  static final long serialVersionUID = 1L;
  private StreamRecord record; //reference to the current record of the stream
  private FgmNodeImpl<?> fgmNode; //the bean that implements the Node. Reference according to the Command Pattern
  private int currentRound;
  private int currentSubround;

  private FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();

  public SendIncreaseOfCCommand(StreamRecord value, FgmNodeImpl<?> fgmNode) {
    this.record = (WorldCupRecord) value;
    this.fgmNode = fgmNode;
    this.currentRound = this.fgmNode.getCurrentRound();
    this.currentSubround = this.fgmNode.getCurrentSubRound();
  }

  @Override
  public void execute() {
    try{
      //logger.info("started executing increaseOfC command");
     //// this.stateMessageBeanWindowed.getIncreaseOfCProducer().beginTransaction();
     //// if (record != null) {
     ////   ////this.fgmNode.getStatisticsMessageProducer().send(new ProducerRecord<>(filePropertiesLocalStore.retrieveString("statistics.messages.topic"), filePropertiesLocalStore.retrieveString("statistics.worldcup.records.messages.key"), StatisticsMessage.getInstance(this.fgmNode.getCurrentRound(), this.fgmNode.getCurrentSubRound(), this.fgmNode.getNodeId(), MessageType.worldCupRecordForStatistics.getMessageType() )));
     ////   this.fgmNode.updateDriftVector(record);
     //// }


      //compute the safe function after the update of the drift vector
      double updateOfSafeFunction = this.fgmNode.getSafeFunction().computePhi(
              this.fgmNode.getXi().getVector(),
              this.fgmNode.getE().getVector(),
              this.fgmNode.getT());

      //if local counter is increased send increase of C message
      if (this.fgmNode.checkIfLocalCounterIsIncreased(
              this.fgmNode.getCi(),
              this.fgmNode.getZi(),
              this.fgmNode.getTheta(), updateOfSafeFunction)) {

        int increaseOfCounter = (int) Math.floor(
                ( updateOfSafeFunction - this.fgmNode.getZi() )
                        / this.fgmNode.getTheta())
                - this.fgmNode.getCi();

        //set the Ci after the increase according to paper
        this.fgmNode.setCi((int)Math.floor(
                (updateOfSafeFunction - this.fgmNode.getZi())
                        / this.fgmNode.getTheta()));

        IncreaseOfCMessage increaseOfCMessage = Factory.getIncreaseOfCMessage();
        increaseOfCMessage.setSiteId(this.fgmNode.getNodeId());
        increaseOfCMessage.setCurrentRound(this.currentRound);
        increaseOfCMessage.setCurrentSubround(this.currentSubround);
        increaseOfCMessage.setIncreaseOfCounter(increaseOfCounter);
        this.fgmNode.getNodeInternalFgmState().getIncreaseOfCProducer().send(
                  new ProducerRecord<>(filePropertiesLocalStore.retrieveString("increaseofc.messages.coordinator.topic"), filePropertiesLocalStore.retrieveString("fgm.increaseofc.messages.topic.key"), increaseOfCMessage));
          //this.fgmNode.getStatisticsMessageProducer().send(new ProducerRecord<>(filePropertiesLocalStore.retrieveString("statistics.messages.topic"), filePropertiesLocalStore.retrieveString("fgm.increaseofc.messages.topic.key"), StatisticsMessage.getInstance(this.fgmNode.getCurrentRound(), this.fgmNode.getCurrentSubRound(), this.fgmNode.getNodeId(), MessageType.increaseOfCMessage.getMessageType() )));
          //RecordMetadata recordMetadata = response.get();
          //producer.flush();
      }
    ////  (this.stateMessageBeanWindowed).getIncreaseOfCProducer().commitTransaction();
    }
    catch(Exception ex){
      logger.info(ex.getMessage());
    }

  }
}




