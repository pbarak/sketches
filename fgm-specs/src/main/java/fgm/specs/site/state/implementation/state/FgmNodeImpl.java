package fgm.specs.site.state.implementation.state;

import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import java.rmi.*;
import java.rmi.server.*;
import fgm.specs.site.commands.*;
import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.common.messages.PhiMessage;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.state.FgmNodeState;
import fgm.specs.data.StreamRecord;
import fgm.specs.factory.IFgmFactory;
import fgm.specs.function.FgmCommand;
import fgm.specs.function.QueryFunction;
import fgm.specs.function.SafeFunction;
import fgm.specs.site.state.LocalStatistic;
import fgm.specs.site.state.NodeRMIInterface;
import fgm.utils.SerializationUtil;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import fgm.specs.common.Vector;

//todo rename to FgmNodeGenericClient
public class FgmNodeImpl<IMPL extends Vector> extends UnicastRemoteObject implements NodeRMIInterface<IMPL>  {
    static Logger logger = Logger.getLogger(FgmNodeImpl.class.getName());
    private final FilePropertiesLocalStore filePropertiesLocalStore;
    private fgm.specs.common.state.FgmNodeState<IMPL> nodeInternalFgmState;
    public FgmNodeImpl(IFgmFactory<IMPL> factory) throws RemoteException {
        this.nodeInternalFgmState = new fgm.specs.common.state.FgmNodeState<>(factory);
        this.filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
    }

    //do not use reference to E of coordinator - use deepCopy instead.
    @Override
    public <T extends Vector> void initializeFgm(GlobalStatistic<T> E, double theta) throws RemoteException{
            this.nodeInternalFgmState.setE( SerializationUtil.deepCopy(E));
            this.nodeInternalFgmState.setTheta( theta );
            this.nodeInternalFgmState.setZi(this.nodeInternalFgmState.getSafeFunction().computePhi(
                    this.nodeInternalFgmState.getXi().getVector(),
                    this.nodeInternalFgmState.getE().getVector(),
                    this.getT()));
            this.nodeInternalFgmState.setCi(0);
	    this.nodeInternalFgmState.setCurrentSubRound(1); //corner case
    }
    public <T extends Vector> void startNewRound(GlobalStatistic<T> E, double theta) throws RemoteException{
        StartNewRoundCommand startNewRoundCommand = new StartNewRoundCommand( theta,  E, this );
        this.executeCommand(startNewRoundCommand);
    }
    public void startNewSubround(double theta) throws RemoteException{
        try{
            StartNewSubroundCommand startNewSubroundCommand = new StartNewSubroundCommand(theta, this);
            this.executeCommand(startNewSubroundCommand);
        }
        catch (Exception e){
            logger.info(e.toString());
        }
    }
    public void subroundEnded() throws RemoteException {
        SendPhiMessageCommand sendPhiMessageCommand = new SendPhiMessageCommand(this);
        this.executeCommand(sendPhiMessageCommand);
    }
    public void roundEnded() throws RemoteException {
        SendXiMessageCommand sendXiMessageCommand = new SendXiMessageCommand(this);
        this.executeCommand(sendXiMessageCommand);
  }
  public void sendIncreaseOfC(StreamRecord value) {
    this.checkIfOutlierViolated(value);
  }

    //to be placed in other interface
    public synchronized boolean checkIfLocalCounterIsIncreased(int ci, double zi, double theta, double update) {
        return ci < (int) Math.floor(( update - zi ) / theta);
    }

    private synchronized void checkIfOutlierViolated(StreamRecord value) {
        SendIncreaseOfCCommand sendIncreaseOfCCommand = new SendIncreaseOfCCommand(value,  this);
        this.executeCommand(sendIncreaseOfCCommand);
    }

    //corner case of the fgm algorithm when the warm up is completed.
    // E and theta is delivered to the sites they deliver it to the Coordinator.
    private synchronized void executeCommand(FgmCommand fgmCommand) {
        fgmCommand.execute();
    }

  public synchronized void updateDriftVector(StreamRecord  record) {
        // at runtime select kind of update based on the config file
      //this.nodeInternalFgmState.getCustomWindow().updateWindow( record);
      //logger.info("entered update Si" + DateTime.now().getMillis() + "i am" + this.getNodeId());
      ///this.nodeInternalFgmState.getSi().update(record, 1);
      //logger.info("leaved update Si" + DateTime.now().getMillis() + "i am" + this.getNodeId());
  }

  public void sendFirstXiMessage()  {
      logger.info("ENTERED IN sending first Xi message" + this.nodeInternalFgmState.getNodeId());
      DriftVectorMessage stateMessage = new DriftVectorMessage();//BeanFactory.getStateMessage();
      stateMessage.setSiteId(this.nodeInternalFgmState.getNodeId());
      //stateMessage.setStateVector((FastAGMS) getXi());
      stateMessage.setStateVector(getXi().getVector());////convertFastAGMSToSerializable((FastAGMS) getXi()));
      stateMessage.setCurrentRound(1);
      stateMessage.setCurrentSubround(0);
      //////final byte[] byteXiMessage = SerializationUtils.serialize(stateMessage);

          try {
           ////   getProducer().beginTransaction();
              this.nodeInternalFgmState.getXiMessagesProducer().send(
                      new ProducerRecord<>(filePropertiesLocalStore.retrieveString("xi.messages.coordinator.topic"),
                              filePropertiesLocalStore.retrieveString("fgm.ximessages.topic.key"), stateMessage)).get();
              //RecordMetadata recordMetadata = response.get();
              //producer.flush();
         ////     getProducer().commitTransaction();
          } catch (Exception ex) {
         ////     getProducer().abortTransaction();
              logger.info(ex.getMessage());
          }
      logger.info("LEAVED  sending first Xi message");
  }
    public synchronized QueryFunction<IMPL> getQueryFunction() {
        return this.nodeInternalFgmState.getQueryFunction();
    }
    public synchronized double getT(){
        return this.nodeInternalFgmState.getQueryFunction().computeQuery(this.nodeInternalFgmState.getE());
    }
    public synchronized LocalStatistic<IMPL> getXi() {
        return this.nodeInternalFgmState.getXi();
    }
    public synchronized GlobalStatistic<IMPL> getE() {
        return this.nodeInternalFgmState.getE();
    }
    public synchronized <K extends Vector> void setE(GlobalStatistic<K> E) {
        this.nodeInternalFgmState.setE(E);
    }
    public synchronized String getNodeId()  {
        return this.nodeInternalFgmState.getNodeId();
    }
    public synchronized double getTheta() {
        return this.nodeInternalFgmState.getTheta();
    }
    public synchronized int getCi() {
        return this.nodeInternalFgmState.getCi();
    }
    public synchronized double getZi() {
        return this.nodeInternalFgmState.getZi();
    }
    public synchronized void setCi(int ci) {
         this.nodeInternalFgmState.setCi(ci);
    }
    public synchronized SafeFunction<IMPL> getSafeFunction() {
        return this.nodeInternalFgmState.getSafeFunction();
    }
    public synchronized int getCurrentRound() {
        return this.nodeInternalFgmState.getCurrentRound();
    }
    public synchronized int getCurrentSubRound() {
        return this.nodeInternalFgmState.getCurrentSubRound();
    }
    public synchronized Producer<String, DriftVectorMessage> getXiMessagesProducer(){ return this.nodeInternalFgmState.getXiMessagesProducer(); }
    public synchronized Producer<String, PhiMessage> getPhiMessagesProducer(){
        return this.nodeInternalFgmState.getPhiMessagesProducer();
    }
    public synchronized Producer<String, IncreaseOfCMessage> getIncreaseOfCProducer(){ return this.nodeInternalFgmState.getIncreaseOfCProducer(); }
    //public synchronized Producer<String, StatisticsMessage> getStatisticsMessageProducer(){ return this.nodeInternalFgmState.getStatisticsMessageProducer(); }
    public synchronized FgmNodeState<IMPL> getNodeInternalFgmState(){
        return this.nodeInternalFgmState;
    }
}





