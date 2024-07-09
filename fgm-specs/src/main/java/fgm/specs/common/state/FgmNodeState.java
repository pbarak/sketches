package fgm.specs.common.state;

import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.common.messages.PhiMessage;
//import fgm.specs.common.messages.StatisticsMessage;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.factory.IFgmFactory;
import fgm.specs.function.QueryFunction;
import fgm.specs.function.SafeFunction;
import fgm.specs.site.state.LocalStatistic;
import org.apache.kafka.clients.producer.Producer;
import org.apache.log4j.Logger;
import java.util.UUID;


public class FgmNodeState<T extends Vector> {

        private  double theta;
        private  double zi;
        private  int ci;
        private final GlobalStatistic<T> E;
        private SafeFunction<T> function;
        private QueryFunction<T> queryFunction;
        //the two vectors below can consist a class named InternalState
        private final LocalStatistic<T> Si;
        private final LocalStatistic<T> Ei;
        private final String nodeId;
        private Producer<String, DriftVectorMessage> xiMessagesProducer;
        private Producer<String, PhiMessage> phiMessagesProducer;
        private Producer<String, IncreaseOfCMessage> increaseOfCMessagesProducer;
        //private Producer<String, StatisticsMessage> statisticsMessageProducer;
        private  int currentRound;
        private int currentSubRound;
        static Logger logger = Logger.getLogger(FgmNodeState.class.getName());
        IFgmFactory<T> factory;

        public FgmNodeState(IFgmFactory<T> factory)
        {
            this.factory = factory;
            this.nodeId = UUID.randomUUID().toString().replace("-", "");
            this.Ei = factory.getLocalStatistic();
            this.Si = factory.getLocalStatistic();
            this.E =  factory.getGlobalStatistic();
            queryFunction = factory.getQueryFunction();
            this.function = factory.getSafeFunction();
            this.ci = 0;
            this.theta = 0.0d;
            this.zi = 0;
            this.currentRound = 1;
            this.currentSubRound = 0;
            Producer<String, DriftVectorMessage> xiMessagesProducer = factory.getXiMessageProducer();
            Producer<String, PhiMessage> phiMessagesProducer = factory.getPhiMessageProducer();
            Producer<String, IncreaseOfCMessage> increaseOfCMessagesProducer = factory.getIncreaseOfCMessageProducer();
        }

        public GlobalStatistic<T> getE() {
            return this.E;
        }

        public <K extends Vector> void setE(GlobalStatistic<K> E) {
            this.E.setVector((T)E.getVector());
        }

        public LocalStatistic<T> getEi() {
            return this.Ei;
        }

        synchronized public LocalStatistic<T> getSi() {
            return this.Si;
        }
        public double getTheta() {
            return this.theta;
        }
        public double getZi() {
            return this.zi;
        }
        public int getCi() {
            return this.ci;
        }
        public void setTheta(double theta) {
            this.theta = theta;
        }
        public void setZi(double zi) {
            this.zi = zi;
        }

        public  LocalStatistic<T> getXi() {
            LocalStatistic<T> result = this.factory.getLocalStatistic();
            result.setVector(this.Si.getVector().subtract(Ei.getVector()));
            return result;
        }
        public SafeFunction<T> getSafeFunction() {
            return this.function;
        }
        public String getNodeId() {
            return this.nodeId;
        }

        public int getCurrentRound() {
            return this.currentRound;
        }
        public int getCurrentSubRound() {
            return this.currentSubRound;
        }
        public void setCi(int ci) {
            this.ci = ci;
        }
        public void setCurrentRound(int currentRound) {
            this.currentRound = currentRound;
        }
        public void setCurrentSubRound(int currentSubRound){ this.currentSubRound = currentSubRound; }
        public void setFunction(SafeFunction<T> function) { // here we should create a class that contains Functions and constraints on E
            this.function = function;
        }
        public double getT(){
        return getQueryFunction().computeQuery(this.getE());
    }
        public QueryFunction<T> getQueryFunction() {
            return this.queryFunction;
        }
        public Producer<String, DriftVectorMessage> getXiMessagesProducer(){ return this.xiMessagesProducer; }
        public Producer<String, PhiMessage> getPhiMessagesProducer(){ return this.phiMessagesProducer; }
        public Producer<String, IncreaseOfCMessage> getIncreaseOfCProducer(){ return this.increaseOfCMessagesProducer; }
        //public Producer<String, StatisticsMessage> getStatisticsMessageProducer(){ return this.statisticsMessageProducer; }
    }


