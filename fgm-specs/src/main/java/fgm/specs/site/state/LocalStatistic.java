package fgm.specs.site.state;

import fgm.specs.common.Vector;
import fgm.specs.data.StreamRecord;

public interface LocalStatistic<T extends Vector> extends Statistic<T>{
    void update(StreamRecord record, int frequency);

}
