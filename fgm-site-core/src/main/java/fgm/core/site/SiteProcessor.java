package fgm.core.site;

import fgm.specs.factory.IFgmFactory;
import fgm.specs.factory.implementation.DependencyInjection;
import fgm.specs.factory.implementation.Factory;
import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.site.state.implementation.state.FgmNodeImpl;
import fgm.specs.data.StreamRecord;
import fgm.specs.site.store.NodeRMICollectionInterface;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.log4j.Logger;

public class SiteProcessor extends AbstractProcessor<String, StreamRecord>{

  //registry to fetch the remote site store and add myself to it
  private Registry registry;

  //generic client
  private FgmNodeImpl<?> stateMessageBean;
  private FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
  static Logger logger = Logger.getLogger(SiteProcessor.class.getName());
  private boolean started = false;
  private static final Integer CASHREGISTERFREQUENCY = 1;

  public SiteProcessor() throws RemoteException {
    try {
      this.stateMessageBean = DependencyInjection.init(filePropertiesLocalStore.retrieveString("factory.name"));
    } catch (Exception e) {
      logger.info(e.toString());
    }
  }

  public static SiteProcessor newInstance() {
    try {
      return new SiteProcessor();
    } catch (RemoteException e) {
      throw new RuntimeException(e);
    }
  }

  public static IFgmFactory<?> getFactory() {
    try {
      return DependencyInjection.getFactory();
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

  //initialization of the processor, this function is called by Kafka Streams
  @SuppressWarnings("unchecked")
  @Override
  public void init(ProcessorContext processorContext) {
    super.init(processorContext);
    
    try {
  	  registry = LocateRegistry.getRegistry(getFactory().getBaseCfg().getRmiHostName(), getFactory().getBaseCfg().getRemoteStorePort());
      NodeRMICollectionInterface stub = (NodeRMICollectionInterface) registry.lookup( getFactory().getBaseCfg().getRemoteStoreBindName());
      stub.add( stateMessageBean);
    } catch (Exception e) {
      logger.info(e.toString());
    }
  }

  @Override
  public void process(String key, StreamRecord value) {
    //here put warm up policy is optional
    this.checkIncreaseOfC(value);
  }

  private void checkIncreaseOfC( StreamRecord value ) {
    if (!started) {
      try {
        stateMessageBean.getNodeInternalFgmState().getSi().update(value, CASHREGISTERFREQUENCY);
        stateMessageBean.sendFirstXiMessage();
        started = true;
      } catch (Exception e) {
        logger.info( e.toString() );
      }
    } else {
      try {
        if (stateMessageBean.getTheta() == 0) {
          try {
            System.out.println("sleep");
            Thread.sleep(1000);
          } catch (Exception e) {
            logger.info(e.toString());
          }
        } else {
          if (value != null) {
            stateMessageBean.getNodeInternalFgmState().getSi().update(value, CASHREGISTERFREQUENCY);
            ////this.fgmNode.getStatisticsMessageProducer().send(new ProducerRecord<>(filePropertiesLocalStore.retrieveString("statistics.messages.topic"), filePropertiesLocalStore.retrieveString("statistics.worldcup.records.messages.key"), StatisticsMessage.getInstance(this.fgmNode.getCurrentRound(), this.fgmNode.getCurrentSubRound(), this.fgmNode.getNodeId(), MessageType.worldCupRecordForStatistics.getMessageType() )));

            //compute the safe function after the update of the drift vector
            double updateOfSafeFunction = stateMessageBean.getSafeFunction().computePhi(
                    stateMessageBean.getXi().getVector(),
                    stateMessageBean.getE().getVector(),
                    stateMessageBean.getT());

            //if local counter is increased send increase of C message
            if (stateMessageBean.checkIfLocalCounterIsIncreased(
                    stateMessageBean.getCi(),
                    stateMessageBean.getZi(),
                    stateMessageBean.getTheta(), updateOfSafeFunction)) {

              int increaseOfCounter = (int) Math.floor(
                      (updateOfSafeFunction - stateMessageBean.getZi())
                              / stateMessageBean.getTheta())
                      - stateMessageBean.getCi();

              //set the Ci after the increase according to paper
              stateMessageBean.setCi((int) Math.floor(
                      (updateOfSafeFunction - stateMessageBean.getZi())
                              / stateMessageBean.getTheta()));
              IncreaseOfCMessage increaseOfCMessage = Factory.getIncreaseOfCMessage();
              increaseOfCMessage.setSiteId(stateMessageBean.getNodeId());
              increaseOfCMessage.setCurrentRound(stateMessageBean.getCurrentRound());
              increaseOfCMessage.setCurrentSubround(stateMessageBean.getCurrentSubRound());
              increaseOfCMessage.setIncreaseOfCounter(increaseOfCounter);
              context().forward(getFactory().getBaseCfg().getIncreaseOfCTopicKey(), increaseOfCMessage);
            }
          }
        }
      }
       catch (Exception ex) {
        logger.info(ex.toString());
      }
    }
  }
}


