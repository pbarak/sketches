package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.collectors.phiscollector;


import fgm.specs.common.cfg.BaseConfiguration;
import fgm.specs.function.implementation.queryfunction.FastAGMSQueryFunction;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.statestore.CoordinatorStateStore;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.state.FgmCoordinatorState;
import fgm.specs.common.messages.PhiMessage;
import fgm.specs.coordinator.collectorsinterfaces.PhisCollectorInterface;
import fgm.specs.site.store.NodeRMICollectionInterface;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.log4j.Logger;

import java.rmi.Naming;

public class PhisCollectionProcessor extends AbstractProcessor<String, PhiMessage> implements PhisCollectorInterface {

    static Logger logger = Logger.getLogger(PhisCollectionProcessor.class.getName());

    //the state store of PhisCollectionProcessor
    private CoordinatorStateStore<?> coordinatorStateStore;
    private NodeRMICollectionInterface remoteNodeStore;
    private final String stateStoreName;
    public PhisCollectionProcessor(String stateStoreName, String rmiServerUrl){
        this.stateStoreName = stateStoreName;
        try {
            remoteNodeStore = (NodeRMICollectionInterface) Naming.lookup(
                    rmiServerUrl);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    //initialization of the processor, this function is called by Kafka Streams
    public void init(ProcessorContext processorContext){
        super.init(processorContext);
        coordinatorStateStore = (CoordinatorStateStore<?>)processorContext.getStateStore(stateStoreName);

    }

    @Override
    public void process(String key, PhiMessage phiMessage) {
        this.collectPhis(phiMessage);
    }


    public void collectPhis(PhiMessage phiMessage) {
//            FgmCoordinatorState fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
//            logPhiMessageReceivedInformation((PhiMessage) phiMessage);
//            if (phiMessage.getCurrentRound() == fgmCoordinatorState.getCurrentRound()
//                    && phiMessage.getCurrentSubround() == fgmCoordinatorState.getCurrentSubround() - 1) {
//                fgmCoordinatorState.getPhis().add(phiMessage);
//                if (fgmCoordinatorState.getPhis().size() == fgmCoordinatorState.getNumberOfNodes()) {
//                    fgmCoordinatorState.setPsi(fgmCoordinatorState.getPhis().calculatePhi());
//                    fgmCoordinatorState.setTheta((((-1) * fgmCoordinatorState.getPsi()) / (Integer.valueOf(2 * fgmCoordinatorState.getNumberOfNodes())).doubleValue()));
//                    logThetaChangedInformation();
//                    if (fgmCoordinatorState.getPsi() >= (fgmCoordinatorState.getEPsi() * fgmCoordinatorState.getNumberOfNodes() * (fgmCoordinatorState.getSafeFunction().computePhiWithZeroXi(
//                            fgmCoordinatorState.getE(), fgmCoordinatorState.getE().getT())))) {
//                        fgmCoordinatorState.setCurrentRound(fgmCoordinatorState.getCurrentRound() + 1);
//                        fgmCoordinatorState.setCurrentSubround(0);
//                        notifyNodesForRoundEnded();
//                    } else {
//                        //create new subround
//                        notifyNodesForNewSubround(fgmCoordinatorState.getTheta());
//                    }
//                }
//            }
        collectMsgAndNotifyNodes ( phiMessage, true );
    }

     void collectMsgAndNotifyNodes(PhiMessage phiMessage, boolean notifyNodes) {
        FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
        logPhiMessageReceivedInformation( phiMessage);
        if (phiMessage.getCurrentRound() == fgmCoordinatorState.getCurrentRound()
                && phiMessage.getCurrentSubround() == fgmCoordinatorState.getCurrentSubround() - 1) {
            fgmCoordinatorState.getPhis().add(phiMessage);
            if (fgmCoordinatorState.getPhis().size() == fgmCoordinatorState.getNumberOfNodes()) {
                fgmCoordinatorState.setPsi(fgmCoordinatorState.getPhis().calculatePhi());
                fgmCoordinatorState.setTheta((((-1) * fgmCoordinatorState.getPsi()) / (Integer.valueOf(2 * fgmCoordinatorState.getNumberOfNodes())).doubleValue()));
                logThetaChangedInformation();
                FastAGMSQueryFunction fastAGMSQueryFunction = new FastAGMSQueryFunction();
                if (fgmCoordinatorState.getPsi() >= (fgmCoordinatorState.getEPsi() * fgmCoordinatorState.getNumberOfNodes() * (fgmCoordinatorState.getSafeFunction().computePhiWithZeroXi(
                        fgmCoordinatorState.getE().getVector(), fastAGMSQueryFunction.computeQuery(fgmCoordinatorState.getE()))))) {
                    fgmCoordinatorState.setCurrentRound(fgmCoordinatorState.getCurrentRound() + 1);
                    fgmCoordinatorState.setCurrentSubround(0);
                    if (notifyNodes) {
                        notifyNodesForRoundEnded();
                    }
                } else {
                    //create new subround
                    if (notifyNodes) {
                        notifyNodesForNewSubround(fgmCoordinatorState.getTheta());
                    }
                }
            }
        }
    }

    public void notifyNodesForNewSubround(double theta) {
        try {
            for (int i = 0; i < remoteNodeStore.getAll().size(); i++) {
                    (remoteNodeStore.getAll().get(i)).startNewSubround(
                            theta);

            } }catch (Exception e) {
            logger.info(e.toString());
        }
    }

    public void notifyNodesForRoundEnded() {
        try {
            for (int i = 0; i < remoteNodeStore.getAll().size(); i++) {
                (remoteNodeStore.getAll().get(i)).roundEnded();
            }
        } catch (Exception e) {
            logger.info(e.toString());
        }
    }

    private void logPhiMessageReceivedInformation(PhiMessage phiMessage) {
        FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
        logger.info("the phiMessage.getCurrentRound() is " + phiMessage.getCurrentRound()
                + " this.currentRound " + fgmCoordinatorState.getCurrentRound()
                + " phiMessage.getCurrentSubround() "
                + phiMessage.getCurrentSubround() + " this.currentSubround "
                + fgmCoordinatorState.getCurrentSubround() + " this.phi is " + phiMessage.getPhiValue()
                + " the site id is " + phiMessage.getSiteId());
    }

    private void logThetaChangedInformation() {
        FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
        logger.info("the new psi is " + fgmCoordinatorState.getPsi());
        logger.info("I CHANGED THETA " + fgmCoordinatorState.getTheta());
        FastAGMSQueryFunction fastAGMSQueryFunction = new FastAGMSQueryFunction();
        logger.info(
                "this.ePsi*this.k*( calculateNorm.zeroArgumentResult(this.E) ) is "
                        + fgmCoordinatorState.getEPsi() * fgmCoordinatorState.getNumberOfNodes() * (fgmCoordinatorState.getSafeFunction().computePhiWithZeroXi(fgmCoordinatorState.getE().getVector(),
                        fastAGMSQueryFunction.computeQuery(fgmCoordinatorState.getE()))));
    }
}
