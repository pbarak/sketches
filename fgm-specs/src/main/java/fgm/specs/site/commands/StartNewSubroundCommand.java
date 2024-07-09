package fgm.specs.site.commands;

import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.site.state.implementation.state.FgmNodeImpl;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.function.FgmCommand;
import org.apache.log4j.Logger;

public class StartNewSubroundCommand implements FgmCommand {
  private final static int initialCiValue = 0;
  private double theta;
  private FgmNodeImpl<?> fgmNode;
  static Logger logger = Logger.getLogger(StartNewSubroundCommand.class.getName());
  FilePropertiesLocalStore filePropertiesLocalStore;
  public StartNewSubroundCommand() {
  }

  public StartNewSubroundCommand(double theta, FgmNodeImpl<?> fgmNode){
    this.fgmNode = fgmNode;
    this.theta = theta;
    filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
  }

  @Override
  public void execute() {
    try {
      GlobalStatistic<?> localE = this.fgmNode.getE();
      this.fgmNode.getNodeInternalFgmState().setTheta(this.theta);

      this.fgmNode.getNodeInternalFgmState().setZi(
              this.fgmNode.getSafeFunction().computePhi(
                      this.fgmNode.getXi().getVector(),
                      localE.getVector(),
                      this.fgmNode.getT()
              ));
      this.fgmNode.setCi(initialCiValue);
      this.fgmNode.getNodeInternalFgmState().setCurrentSubRound(this.fgmNode.getCurrentSubRound() + 1);
      //this.fgmNode.getStatisticsMessageProducer().send(new ProducerRecord<>(filePropertiesLocalStore.retrieveString("statistics.messages.topic"), filePropertiesLocalStore.retrieveString("fgm.newsubround.topic.key"), StatisticsMessage.getInstance(this.fgmNode.getCurrentRound(), this.fgmNode.getCurrentSubRound(), this.fgmNode.getNodeId(), MessageType.statNewSubroundMessage.getMessageType())));
    } catch (Exception e){
      logger.info(e.toString());
    }
  }

}
