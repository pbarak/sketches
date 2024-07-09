package fgm.specs.site.commands;

import fgm.specs.site.state.implementation.state.FgmNodeImpl;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.function.FgmCommand;
import fgm.utils.SerializationUtil;
import org.apache.log4j.Logger;

public class StartNewRoundCommand implements FgmCommand {
  static Logger logger = Logger.getLogger(StartNewRoundCommand.class.getName());
  private final static int initialCiValue = 0;
  private double theta;
  private GlobalStatistic<? extends Vector> localE;
  private FgmNodeImpl<? extends Vector> fgmNode;
  public StartNewRoundCommand(){
  }

  public StartNewRoundCommand(double theta, GlobalStatistic<? extends Vector> E,
                              FgmNodeImpl<? extends Vector> fgmNode){
    this.theta = theta;
    this.localE = E;
    this.fgmNode = fgmNode;
  }

  @Override
  public void execute() {
    try {
      this.fgmNode.setE(this.localE);
      this.fgmNode.getNodeInternalFgmState().getEi().setVector(SerializationUtil.deepCopy(this.fgmNode.getNodeInternalFgmState().getSi().getVector()));
      this.fgmNode.getNodeInternalFgmState().setTheta(this.theta);
      this.fgmNode.getNodeInternalFgmState().setZi(this.fgmNode.getSafeFunction().computePhi(
              this.fgmNode.getXi().getVector(),
              this.fgmNode.getE().getVector(),
              this.fgmNode.getT()
      ));

      this.fgmNode.setCi(initialCiValue);
      logger.info("LEAVED START_NEW_ROUND");
    } catch (Exception e) {
      logger.info(e.toString());
    }
  }
}
