package fgm.specs.function;

import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;

public interface QueryFunction<IMPL extends Vector> {
     <IMPL extends Vector> Double computeQuery(GlobalStatistic<IMPL> globalStatistic);
}
