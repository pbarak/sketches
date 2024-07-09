package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.state;

import fgm.specs.site.state.implementation.state.FgmStatistic;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;

public class FgmGlobalStatistic<T extends Vector> extends FgmStatistic<T> implements GlobalStatistic<T> {
    public FgmGlobalStatistic(T vector) {
        super(vector);
    }

}
