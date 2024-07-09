package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.collectors.xiscollector;

import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.xicollection.model.DriftVectorEntry;
import fgm.specs.factory.IFgmFactory;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.statestore.CoordinatorStateStore;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.state.FgmCoordinatorState;
import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.coordinator.collectorsinterfaces.XisCollectorInterface;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.function.QueryFunction;
import fgm.specs.site.state.NodeRMIInterface;
import fgm.specs.site.store.NodeRMICollectionInterface;
import fgm.utils.SerializationUtil;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.log4j.Logger;
import java.rmi.Naming;

public class XisCollectionProcessor extends AbstractProcessor<String, DriftVectorMessage> implements XisCollectorInterface {

    static Logger logger = Logger.getLogger(XisCollectionProcessor.class.getName());
    private CoordinatorStateStore<?> coordinatorStateStore;
    private NodeRMICollectionInterface remoteStoreCollection;
    private final String stateStoreName;
    private IFgmFactory<?> factory;

    private static final Double NEGATIVECASHREGISTERFREQUENCY = -1.0;
    public XisCollectionProcessor(String stateStoreName, String rmiServerUrl, IFgmFactory<?> factory){
        this.factory = factory;
        this.stateStoreName = stateStoreName;
        try {
            remoteStoreCollection = (NodeRMICollectionInterface) Naming.lookup(
                    rmiServerUrl);
        } catch (Exception e) {
            logger.info( e.getMessage() );
        }
    }

    //initialization of the processor, this function is called by Kafka Streams
    public void init(ProcessorContext processorContext){
        super.init(processorContext);
        coordinatorStateStore = processorContext.getStateStore(stateStoreName);
    }

    @Override
    public void process(String key, DriftVectorMessage stateMessage) {
        this.updateGlobalEstimate(stateMessage);
    }
    public void updateGlobalEstimate(DriftVectorMessage driftVectorMessage) {
        //The Coordinator maintains a Xis Collection that is the nodes DriftVectors Collection
        //From the nodes DriftVector Collection, Eis Collections is created according to paper
        //From Eic Collection the Global Statistic collection is created/updated according to paper.
            logReceivedXiInformation(driftVectorMessage);
            FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();

            if (driftVectorMessage.getCurrentRound() == fgmCoordinatorState.getCurrentRound()
                    && driftVectorMessage.getCurrentSubround() == fgmCoordinatorState.getCurrentSubround()) {

                //collect all Xi from sites in hashmap
                populateXisCollection(driftVectorMessage);

                if (fgmCoordinatorState.getXisCollection().getXis().size() == fgmCoordinatorState.getNumberOfNodes()) {
                    populateEisCollection();
//                    for (int i = 0; i < fgmCoordinatorState.getNumberOfNodes(); i++) {
//                        fgmCoordinatorState.getXis().get(i).getValue().normalize(fgmCoordinatorState.getNumberOfNodes());
//                    }
//                    if (fgmCoordinatorState.getEis().isEmpty()) {
//                        for (int i = 0; i < fgmCoordinatorState.getNumberOfNodes(); i++) {
//                            fgmCoordinatorState.getEis().add(
//                                    new AbstractMap.SimpleEntry<>(fgmCoordinatorState.getXis().get(i).getKey(),
//                                            fgmCoordinatorState.getXis().get(i).getValue()));
//                        }
//                    } else {
//                        for (int i = 0; i < fgmCoordinatorState.getNumberOfNodes(); i++) {
//                            for (int k = 0; k < fgmCoordinatorState.getNumberOfNodes(); k++) {
//                                if (fgmCoordinatorState.getEis().get(i).getKey().equals(
//                                        fgmCoordinatorState.getXis().get(k).getKey())) {
//                                   SketchHelper.addToFastAGMSSketch( fgmCoordinatorState.getEis().get(i).getValue(),
//                                            fgmCoordinatorState.getXis().get(k).getValue() );
//                                }
//                            }
//                        }
//                    }
                    createAndFlushEStatistic(true);
                }
            }
    }
    private void populateEisCollection () {
        FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
        for (int i = 0; i < fgmCoordinatorState.getNumberOfNodes(); i++) {
            //SketchHelper.divideWithNumOfNodes(fgmCoordinatorState.getXisCollection().getEntry(i).getXiNodeDriftVector(), fgmCoordinatorState.getNumberOfNodes());
            fgmCoordinatorState.getXisCollection().getEntry(i).getXiNodeDriftVector().scale((double)fgmCoordinatorState.getNumberOfNodes());
            //fgmCoordinatorState.getXisCollection().getXis().get(i).getXiNodeDriftVector().normalize(fgmCoordinatorState.getNumberOfNodes());

        }
        if (fgmCoordinatorState.getEisCollection().getEis().isEmpty()) {
            for (int i = 0; i < fgmCoordinatorState.getNumberOfNodes(); i++) {
//                fgmCoordinatorState.getEis().add(
//                        new AbstractMap.SimpleEntry<>(fgmCoordinatorState.getXisCollection().getXis().get(i).getXiNodeId(),
//                                fgmCoordinatorState.getXisCollection().getXis().get(i).getXiNodeDriftVector()));
                fgmCoordinatorState.getEisCollection().collectEi(fgmCoordinatorState.getXisCollection().getEntry(i));
            }
        } else {
            for (int i = 0; i < fgmCoordinatorState.getNumberOfNodes(); i++) {
                for (int k = 0; k < fgmCoordinatorState.getNumberOfNodes(); k++) {
//                    if (fgmCoordinatorState.getEis.get(i).getKey().equals(
//                            fgmCoordinatorState.getXisCollection().getXis().get(k).getXiNodeId())) {
//                        SketchHelper.addToFastAGMSSketch( fgmCoordinatorState.getEis().get(i).getValue(),
//                                fgmCoordinatorState.getXisCollection().getXis().get(k).getXiNodeDriftVector() );
//                    }
                    if (fgmCoordinatorState.getEisCollection().getEntry(i).getEiNodeId()
                            .equals(fgmCoordinatorState.getXisCollection().getEntry(k).getXiNodeId())) {
                        fgmCoordinatorState.getEisCollection().getEntry(i).getEiLocalStatistic().add(fgmCoordinatorState.getXisCollection().getEntry(k).getXiNodeDriftVector());
                    }
                }
            }
        }
    }

    private void createAndFlushEStatistic(boolean notifyNodes) {
        FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
        GlobalStatistic<?> globalStatistic1 = SerializationUtil.deepCopy(fgmCoordinatorState.getE());
        globalStatistic1.getVector().scale(NEGATIVECASHREGISTERFREQUENCY);
        fgmCoordinatorState.getE().getVector().add(globalStatistic1.getVector());

        //fgmCoordinatorState.getE().getVector()..clear();
        for ( int k = 0; k < fgmCoordinatorState.getNumberOfNodes(); k++ ) {
            fgmCoordinatorState.getE().addVectors(fgmCoordinatorState.getEisCollection().getEntry(k).getEiLocalStatistic());
        }
        fgmCoordinatorState.getXisCollection().getXis().clear();
        QueryFunction<?> fastAGMSQueryFunction = factory.getQueryFunction();
        logger.info("THE RESULT IS " + fastAGMSQueryFunction.computeQuery(fgmCoordinatorState.getE()));
        try {
            fgmCoordinatorState.setCurrentSubround(0);
            fgmCoordinatorState.setPsi(fgmCoordinatorState.getNumberOfNodes() * fgmCoordinatorState.getSafeFunction().computePhiWithZeroXi(fgmCoordinatorState.getE().getVector(),
                    fastAGMSQueryFunction.computeQuery(fgmCoordinatorState.getE())));
            fgmCoordinatorState.setTheta((((-1) * fgmCoordinatorState.getPsi()) / (Integer.valueOf(2 * fgmCoordinatorState.getNumberOfNodes())).doubleValue()));

            if(notifyNodes){
                this.notifyNodesForNewRound();
            }

            fgmCoordinatorState.setStarted(true);
        } catch (Exception e) {
            logger.info(e.toString());
        }
    }

    public void notifyNodesForNewRound() {
        FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
        try{
            for (int i = 0; i < remoteStoreCollection.getAll().size(); i++) {
                NodeRMIInterface<?> stateMessageBean = (remoteStoreCollection.getAll().get(
                        i));
                if ( (!fgmCoordinatorState.getIfStarted()) ) {
                        stateMessageBean.initializeFgm( fgmCoordinatorState.getE()  , fgmCoordinatorState.getTheta() );
                    fgmCoordinatorState.setCurrentSubround(1);
                } else {
                    stateMessageBean.startNewRound( fgmCoordinatorState.getE() ,  fgmCoordinatorState.getTheta() );
                }
            }
        }
        catch (Exception ex) {
            logger.info(ex.toString());
        }
    }
    private void populateXisCollection(DriftVectorMessage stateMessage) {
        FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
        boolean found = false;
        if ( !fgmCoordinatorState.getXisCollection().getXis().isEmpty() ) {
            for (DriftVectorEntry<?> xi : fgmCoordinatorState.getXisCollection().getXis()) {
                if (xi.getXiNodeId().equals(stateMessage.getSiteId())) {
                    found = true;
                }
            }
            if (!found) { // the DriftVectorEntry should not already exist in the collection
                fgmCoordinatorState.getXisCollection().collectXi( stateMessage );
            }
        } else {
            fgmCoordinatorState.getXisCollection().collectXi( stateMessage );
        }
    }

    private void logReceivedXiInformation(DriftVectorMessage stateMessage) {
        FgmCoordinatorState<?> fgmCoordinatorState = coordinatorStateStore.getFgmCoordinatorState();
        logger.info("the XiMessage.getCurrentRound() is "
                + stateMessage.getCurrentRound() + " this.currentRound "
                + fgmCoordinatorState.getCurrentRound() + " XiMessage.getCurrentSubround() "
                + stateMessage.getCurrentSubround() + " this.currentSubRound "
                + fgmCoordinatorState.getCurrentSubround() + " the site id is "
                + (stateMessage).getSiteId());
    }
}
