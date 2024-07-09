package fgm.specs.function.implementation.queryfunction;

import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.common.vector.KeyFrequencyList;
import fgm.specs.function.QueryFunction;

public class KeyFrequencyListQFunction implements QueryFunction<KeyFrequencyList> {

    public <IMPL extends Vector> Double computeQuery(GlobalStatistic<IMPL> globalStatistic) {  //this is the query function of the global estimate
        double update = 0;
        int i = 0;
        while (i < ((KeyFrequencyList) globalStatistic.getVector()).size()) {
            update = update + ((KeyFrequencyList) globalStatistic.getVector()).getStateVector().get(i).getValue().doubleValue() *
                    ((KeyFrequencyList) globalStatistic.getVector()).getStateVector().get(i).getValue().doubleValue();
            i++;
        }
        return Math.sqrt(update);
    }
}
