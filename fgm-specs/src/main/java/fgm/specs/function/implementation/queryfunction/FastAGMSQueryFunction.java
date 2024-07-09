package fgm.specs.function.implementation.queryfunction;

import fgm.specs.common.vector.FastAGMS;
import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.function.QueryFunction;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.Arrays;

public class FastAGMSQueryFunction implements QueryFunction<FastAGMS> {
    public <IMPL extends Vector> Double computeQuery(GlobalStatistic<IMPL> globalStatistic){  //this is the query function of the global estimate
        return medianOfInnerProducts((GlobalStatistic<FastAGMS>) globalStatistic); //according to the paper this cast is guaranteed  tp
    }

    private double medianOfInnerProducts(GlobalStatistic<FastAGMS> globalFastAGMSStatistic) {
        //self-join size estimate
        int depth = (globalFastAGMSStatistic.getVector()).depth();
        double[] innerProductsArray = new double[depth];

        double update = 0.0;
        int j = 0;
        for(int i = 0; i < depth; i++) {

            Double [] sketchColumn = ((FastAGMS)globalFastAGMSStatistic.getVector()).getSketchColumn(i);
            while (j < sketchColumn.length) {
                update = update + sketchColumn[j] * sketchColumn[j];
                j++;
            }
            innerProductsArray[i] = update;
            update = 0.0;
            j = 0;
        }
        Arrays.sort(innerProductsArray);
        Median median = new Median();
        double medianValue = median.evaluate(innerProductsArray);
        return medianValue;
    }

}
