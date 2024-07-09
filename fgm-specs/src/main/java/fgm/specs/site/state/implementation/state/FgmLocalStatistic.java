package fgm.specs.site.state.implementation.state;

import fgm.specs.common.Vector;
import fgm.specs.site.state.LocalStatistic;
import fgm.specs.data.StreamRecord;

public class FgmLocalStatistic<T extends Vector> extends FgmStatistic<T> implements LocalStatistic<T>  {
    public FgmLocalStatistic() {super();};
    public FgmLocalStatistic(T vector) {
        super(vector);
    }
    @Override
    public void update(StreamRecord record, int frequency) {
        this.getVector().update(record, frequency);
    }

}
