package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.collectors.increaseofc;

import fgm.specs.common.cfg.BaseConfiguration;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.statestore.CoordinatorStateStore;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.state.FgmCoordinatorState;
import fgm.specs.coordinator.collectorsinterfaces.IncreaseOfCsCollectorInterface;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.site.state.NodeRMIInterface;
import fgm.specs.site.store.NodeRMICollectionInterface;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.rmi.Naming;

public class IncreaseOfCCollectionProcessor extends AbstractProcessor<String, IncreaseOfCMessage> implements IncreaseOfCsCollectorInterface {

    static Logger logger = Logger.getLogger(IncreaseOfCCollectionProcessor.class.getName());
    //in order to retrieve the StateStore object you have to provide the State Store name to the processor context.
    private CoordinatorStateStore<?> coordinatorStateStore;
    private final String stateStoreName;
    private NodeRMICollectionInterface remoteNodeStore;
    private FgmCoordinatorState<?> fgmCoordinatorState;
    public IncreaseOfCCollectionProcessor(String stateStoreName, String rmiServerUrl, BaseConfiguration<?> baseConfiguration){
        this.stateStoreName = stateStoreName;
        try {
            remoteNodeStore = (NodeRMICollectionInterface) Naming.lookup(
                    rmiServerUrl);
        } catch (Exception e) {
           logger.info( e.getMessage());
        }
    }

    public static IncreaseOfCCollectionProcessor newInstance(String stateStoreName, String rmiServerUrl, BaseConfiguration<?> baseConfiguration){
        return new IncreaseOfCCollectionProcessor(stateStoreName, rmiServerUrl, baseConfiguration);
    }

    //initialization of the processor, this function is called by Kafka Streams
    public void init(ProcessorContext processorContext){
        super.init(processorContext);
        coordinatorStateStore = processorContext.getStateStore(stateStoreName);
        fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
    }

    @Override
    public void process(String key, IncreaseOfCMessage increaseOfCMessage) {
        this.collectIncreaseOfCs(increaseOfCMessage);
    }

    private void logIncreaseOfCReceivedInformation(IncreaseOfCMessage increaseOfCMessage) {
        FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
        logger.info("the increaseOfCMessage.getCurrentRound() is "
                + increaseOfCMessage.getCurrentRound() + " this.currentRound "
                + fgmCoordinatorState.getCurrentRound() + " increaseOfCMessage.getCurrentSubround() "
                + increaseOfCMessage.getCurrentSubround() + " this.currentSubRound "
                + fgmCoordinatorState.getCurrentSubround());
    }

    public void notifyNodesForSubroundEnded(){
        try {
            for (NodeRMIInterface<?> site : remoteNodeStore.getAll() ) {
                    site.subroundEnded();
            }
        }
        catch (Exception e) {
            logger.info("Error in collect Increase of C: " + e);
        }
    }


    public void collectIncreaseOfCs(IncreaseOfCMessage increaseOfCMessage) {

            logIncreaseOfCReceivedInformation(increaseOfCMessage);

//            if (fgmCoordinatorState.getSiteIds().size() < fgmCoordinatorState.getNumberOfNodes()) {
//                fgmCoordinatorState.addSiteId((increaseOfCMessage).getSiteId());
//            }

//            if (increaseOfCMessage.getCurrentRound() == fgmCoordinatorState.getCurrentRound()
//                    && increaseOfCMessage.getCurrentSubround() == fgmCoordinatorState.getCurrentSubround() ) { // && fgmCoordinatorState.getSiteIds().size() == fgmCoordinatorState.getNumberOfNodes()) {
//                // form global C according to paper and notify nodes for subround ended
//                fgmCoordinatorState.setGlobalC(fgmCoordinatorState.getGlobalC() + (increaseOfCMessage).getStatistic().intValue());
//
//                if (fgmCoordinatorState.getGlobalC() > fgmCoordinatorState.getNumberOfNodes()) {
//                    fgmCoordinatorState.setGlobalC(0);
//                    fgmCoordinatorState.setCurrentSubround(fgmCoordinatorState.getCurrentSubround() + 1);
//                    notifyNodesForSubroundEnded();
//                }
//            }
            collectMsgAndNotifyNodes( increaseOfCMessage,true );
    }

     void collectMsgAndNotifyNodes( IncreaseOfCMessage increaseOfCMessage, boolean notifyNodes ){
        if (increaseOfCMessage.getCurrentRound() == fgmCoordinatorState.getCurrentRound()
                && increaseOfCMessage.getCurrentSubround() == fgmCoordinatorState.getCurrentSubround() ) {
            // form global C according to paper and notify nodes for subround ended
            fgmCoordinatorState.setGlobalC(fgmCoordinatorState.getGlobalC() + increaseOfCMessage.getIncreaseOfCounter());
            if (fgmCoordinatorState.getGlobalC() > fgmCoordinatorState.getNumberOfNodes()) {
                fgmCoordinatorState.setGlobalC(0);
                fgmCoordinatorState.setCurrentSubround(fgmCoordinatorState.getCurrentSubround() + 1);
                if(notifyNodes) {
                    notifyNodesForSubroundEnded();
                }
            }
        }
    }
}
