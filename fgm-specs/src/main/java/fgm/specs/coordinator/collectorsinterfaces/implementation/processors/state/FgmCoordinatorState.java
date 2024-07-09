package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.state;

import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.factory.IFgmFactory;
import fgm.specs.function.SafeFunction;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.xicollection.model.DriftVectorsCollection;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.xicollection.model.EisCollection;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class FgmCoordinatorState<T extends Vector> {
    int numberOfNodes; //this is the num of sites
    double theta;
    double ePsi;
    double psi;
    int globalCounter;
    int currentRound;
    int currentSubround;
    boolean started;
    GlobalStatistic<T> E; //global estimated statistic
    SafeFunction<T> safeFunction;
    DriftVectorsCollection Xis;
    EisCollection Eis;
    PhiMessageCollection phis;
    ArrayList<String> siteIds;
    static Logger logger = Logger.getLogger(FgmCoordinatorState.class.getName());
    public FgmCoordinatorState(){
    }

    public FgmCoordinatorState(IFgmFactory<T> factory) {
        logger.info("ENTERED in FgmCoordinator State initialization");
        //POJO of the internal state of coordinator according to paper
        FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
        numberOfNodes = factory.getBaseCfg().getNumberOfNodes();
        theta = 0.0d;
        ePsi = 0.005d;
        psi = 0.0f;
        globalCounter = 0;
        currentRound = 1;
        currentSubround = 0;
        started = false;
        E = factory.getGlobalStatistic();
        safeFunction = factory.getSafeFunction();
        Xis = new DriftVectorsCollection();
        Eis = new EisCollection();
        phis = new PhiMessageCollection();
        siteIds = new ArrayList<>();
    }

    public int getNumberOfNodes() {
        return this.numberOfNodes;
    }
    public double getTheta(){
        return this.theta;
    }
    public double getEPsi(){
        return this.ePsi;
    }
    public double getPsi(){
        return this.psi;
    }
    public int getGlobalC(){
        return globalCounter;
    }
    public int getCurrentRound(){
        return this.currentRound;
    }
    public int getCurrentSubround(){
        return this.currentSubround;
    }
    public boolean getIfStarted(){
        return this.started;
    }
    public GlobalStatistic<T> getE() {
        return this.E;
    }
    public SafeFunction<T> getSafeFunction() {
        return this.safeFunction;
    }
    public DriftVectorsCollection getXisCollection() {
        return this.Xis;
    }
    public EisCollection getEisCollection() {
        return this.Eis;
    }
    public PhiMessageCollection getPhis() {
        return this.phis;
    }
    public void setGlobalC(int globalCounter){
        this.globalCounter = globalCounter;
    }
    public void setPsi(double psi){
        this.psi = psi;
    }
    public void setCurrentRound(int round) {
        this.currentRound = round;
    }
    public void setCurrentSubround(int currentSubround){
        this.currentSubround = currentSubround;
    }
    public void setTheta(double theta) {
        this.theta = theta;
    }
    public void setStarted(boolean started){
        this.started = started;
    }
    public ArrayList<String> getSiteIds(){
        if(this.siteIds == null) {
            return new ArrayList<>();
        } else {
            return this.siteIds;
        }
    }
    public void addSiteId(String siteId){
        this.getSiteIds().add(siteId);
    }
}

