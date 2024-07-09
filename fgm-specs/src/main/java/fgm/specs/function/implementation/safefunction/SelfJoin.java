package fgm.specs.function.implementation.safefunction;

import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.vector.FastAGMS;
import fgm.specs.function.SafeFunction;
import java.util.*;
import fgm.specs.common.Vector;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.log4j.Logger;

public class SelfJoin implements  SafeFunction<FastAGMS> {
//we create for local drift vector a boolean array with the same dimensionality as that of the depth of the sketch.
// if each row's inner product violates the higher (and lower) bound outlier(s) it is set to false.
//we count each number of true elements, (n - p1) and (n - p2) as per paper, and we calculate all the possible combinations.

  static final long serialVersionUID = 1L; //assign a long value
  private boolean[] upperBoundBooleanArray; //a helper array for boolean selection of the indexes of the sketch row upper bound outliers
  private boolean[] lowerBoundBooleanArray; //a helper array for boolean selection of the indexes of the sketch row lower bound outliers
  private int[] tempUpperBoundBooleanArray; //a helper array for the indexes of the sketch row upper bound outliers
  private int[] tempLowerBoundBooleanArray; //a helper array for the indexes of the sketch row lower bound outliers
  private final FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
  static Logger logger = Logger.getLogger(SelfJoin.class.getName());
  public SelfJoin() {
  }

@Override
  public <IMPL extends Vector> double computePhi(IMPL X, IMPL E, Double T) {

    //the computation depends on the specialization of Local and Global Statistics classes that is FastAGMS

    FastAGMS driftVector = (FastAGMS) X; //according to paper it is guaranteed for the cast to be successful
    FastAGMS estimation = (FastAGMS) E;
    int depth = estimation.depth();
    upperBoundBooleanArray = new boolean[depth];
    lowerBoundBooleanArray = new boolean[depth];

    for (int i = 0; i < depth; i++) {
      upperBoundBooleanArray[i] = true;
      lowerBoundBooleanArray[i] = true;
    }

    int p1 = 0; // p1
    int p2 = 0; // p2


    for (int i = 0; i < estimation.depth(); i++) {
      if (innerProduct(estimation.getSketchColumn(i)) >= filePropertiesLocalStore.retrieveDouble("higher.bound.outlier") * T) {
        upperBoundBooleanArray[i] = false;
        p1++;
      }
    }

    tempUpperBoundBooleanArray = new int[estimation.depth() - p1];
    int index1 = 0;
    for (int i = 0; i < estimation.depth(); i++) {
      if (upperBoundBooleanArray[i]) {
        tempUpperBoundBooleanArray[index1++] = i;
      }
    }

    for (int i = 0; i < estimation.depth(); i++) {
      if (innerProduct(estimation.getSketchColumn(i)) <= filePropertiesLocalStore.retrieveDouble("lower.bound.outlier") * T) {
        lowerBoundBooleanArray[i] = false;
        p2++;
      }
    }

    tempLowerBoundBooleanArray = new int[estimation.depth() - p2];
    int index2 = 0;
    for (int i = 0; i < estimation.depth(); i++) {
      if (lowerBoundBooleanArray[i]) {
        tempLowerBoundBooleanArray[index2++] = i;
      }
    }

    int majority = majorityNumber(depth); // n depth of sketch

    // find all the combinations of n - p + 1 - majority of those indexes of booleanArray that are true. (m)
    boolean started = false;
    double maxPhiPlusValue = 0.0;
     	Iterator<int[]> iterator1 = CombinatoricsUtils.combinationsIterator(estimation.depth() - p1, estimation.depth() - p1 + 1 - majority);
	    while (iterator1.hasNext()) {
      		 int[] combination1 = iterator1.next();
      		double temp = phiPlus(combination1, driftVector, estimation, T);
      		if (!started) {
              maxPhiPlusValue = temp;
	          started = true;
            }
      		else {
              if(maxPhiPlusValue < temp) {
                maxPhiPlusValue = temp;
              }
            }
    	   }
    int m = 0;

    boolean started1 = false;
    double maxPhiMinusValue = 0.0;

      Iterator<int[]> iterator2 = CombinatoricsUtils.combinationsIterator(estimation.depth() - p2, estimation.depth() - p2 + 1 - majority);
      while (iterator2.hasNext()) {
        m = m + 1;
        int[] combination2 = iterator2.next();
        double temp = phiMinus(combination2, driftVector, estimation, T);
        if (!started1) {
          maxPhiMinusValue = temp;
          started1 = true;
        }
        else {
          if(maxPhiMinusValue < temp) {
            maxPhiMinusValue = temp;
          }
        }
      }

    if (maxPhiMinusValue <= maxPhiPlusValue) {
      return maxPhiPlusValue;
    }
    else {
      return maxPhiMinusValue;
    }

  }

  public double innerProduct(Double[] column) {
    double update = 0.0;
    int i = 0;
    while (i < column.length) {
      update = update + column[i] * column[i];
      i++;
    }
    return update;
  }

  public int majorityNumber(int depth) {
    if ((depth % 2) == 0) {
      return (depth / 2) + 1;
    } else {
      return (int) Math.ceil(depth / 2.0);
    }
  }

  public double phiPlus(int[] columns, FastAGMS X, FastAGMS E, double T) {
    double update1 = 0.0;
    double update2 = 0.0;
    for (int i = 0; i < columns.length; i++) {
      update1 =
              update1 +
                      Math.abs(phiPlusZeroArgumentColumnFunction(E, tempUpperBoundBooleanArray[columns[i]], T))
                      * phiPlusColumnFunction(X.getSketchColumn(tempUpperBoundBooleanArray[columns[i]]), E, tempUpperBoundBooleanArray[columns[i]], T);
      
      double tmp = phiPlusZeroArgumentColumnFunction(E, tempUpperBoundBooleanArray[columns[i]], T);
      update2 = update2 + Math.abs(tmp) * Math.abs(tmp);
    }
    return update1 / Math.sqrt(update2);
  }

//  public double phiZeroXiPlus(int[] columns, FastAGMS E, double T) {
//    //logger.info("The T0 is" + T);
//    double update1 = 0.0;
//    double update2 = 0.0;
//    //logger.info("columns.length is " + columns.length);
//    for (int i = 0; i < columns.length; i++) {
//      update1 =
//              update1 +
//                      Math.abs(phiPlusZeroArgumentColumnFunction(E, tempUpperBoundBooleanArray[i], T) )
//                      * phiPlusZeroArgumentColumnFunction(E, tempUpperBoundBooleanArray[i], T);
//
//      double tmp = phiPlusZeroArgumentColumnFunction(E, tempUpperBoundBooleanArray[i], T);
//      update2 = update2 + Math.abs(tmp) * Math.abs(tmp);
//    }
//    //logger.info("plus update1 is " + update1);
//    //logger.info("plus update2 is " + update2);
//    //logger.info("plus update1 / Math.sqrt(update2)" + update1 / Math.sqrt(update2));
//    return update1 / Math.sqrt(update2);
//  }


  public double phiZeroXiPlus(int[] columns, FastAGMS E, double T) {
    double update1 = 0.0;
    double update2 = 0.0;
    for (int i = 0; i < columns.length; i++) {
      update1 =
              update1 +
                      Math.abs(phiPlusZeroArgumentColumnFunction(E, tempUpperBoundBooleanArray[columns[i]], T))
                              * phiPlusZeroArgumentColumnFunction(E, tempUpperBoundBooleanArray[columns[i]], T);

      double tmp = phiPlusZeroArgumentColumnFunction(E, tempUpperBoundBooleanArray[columns[i]], T);
      update2 = update2 + Math.abs(tmp) * Math.abs(tmp);
    }
    return update1 / Math.sqrt(update2);
  }

  public double phiMinus(int[] columns, FastAGMS X, FastAGMS E, double T) {
    double update1 = 0.0;
    double update2 = 0.0;
    for (int i = 0; i < columns.length; i++) {
      update1 =
              update1 +
                      Math.abs(phiMinusZeroArgumentColumnFunction(E, tempLowerBoundBooleanArray[columns[i]], T))
                      * phiMinusColumnFunction(X.getSketchColumn(i), E, tempLowerBoundBooleanArray[columns[i]], T);
      double tmp = phiMinusZeroArgumentColumnFunction(E, tempLowerBoundBooleanArray[columns[i]], T);
      update2 = update2 + Math.abs(tmp) * Math.abs(tmp);
    }
    return update1 / Math.sqrt(update2);
  }

//  public double phiZeroXiMinus(int[] columns, FastAGMS E, double T) {
//    double update1 = 0.0;
//    double update2 = 0.0;
//    for (int i = 0; i < columns.length; i++) {
//      update1 =
//              update1 +
//                      Math.abs(phiMinusZeroArgumentColumnFunction(E, columns[i], T) )
//                      * phiMinusZeroArgumentColumnFunction( E, columns[i], T);
//      double tmp = phiMinusZeroArgumentColumnFunction(E, columns[i], T);
//      update2 = update2 + Math.abs(tmp) * Math.abs(tmp);
//    }
//    //logger.info("minus update1 is " + update1);
//    //logger.info("minus update2 is " + update2);
//    //logger.info("minus update1 / Math.sqrt(update2)" + update1 / Math.sqrt(update2));
//    return update1 / Math.sqrt(update2);
//  }

  public double phiZeroXiMinus(int[] columns, FastAGMS E, double T) {
    double update1 = 0.0;
    double update2 = 0.0;
    for (int i = 0; i < columns.length; i++) {
      update1 =
              update1 +
                      Math.abs(phiMinusZeroArgumentColumnFunction(E, tempLowerBoundBooleanArray[columns[i]], T))
                              * phiMinusZeroArgumentColumnFunction( E, tempLowerBoundBooleanArray[columns[i]], T);
      double tmp = phiMinusZeroArgumentColumnFunction(E, tempLowerBoundBooleanArray[columns[i]], T);
      update2 = update2 + Math.abs(tmp) * Math.abs(tmp);
    }
    return update1 / Math.sqrt(update2);
  }

  public double phiPlusColumnFunction(Double[] x, FastAGMS E, int column, double T) {
    double update1 = 0.0;

    int j = 0;
    Double[] Ei = E.getSketchColumn(column);
    while (j < x.length && j < Ei.length) {
        update1 = update1 + (x[j] + Ei[j]) * (x[j] + Ei[j]);
        j++;
    }

    return (Math.sqrt(update1) - Math.sqrt(filePropertiesLocalStore.retrieveDouble("higher.bound.outlier")*T));
  }

  public double phiPlusZeroArgumentColumnFunction(FastAGMS E, int column, double T) {
    double update1 = 0.0;

    int j = 0;
    Double[] Ei = E.getSketchColumn(column);
    while (j < Ei.length) {
      update1 = update1 + (Ei[j]) * (Ei[j]);
      j++;
    }
    return (Math.sqrt(update1) - Math.sqrt(filePropertiesLocalStore.retrieveDouble("higher.bound.outlier")*T));
  }

  public double phiMinusColumnFunction(Double[] x, FastAGMS E, int column, double T) {
    double update1 = 0.0;

    Double[] Ei = E.getSketchColumn(column);
    assert(x.length == Ei.length);
    double update2 = 0.0;
    int i = 0;
    while (i < Ei.length) {
      update2 = update2 + Ei[i] * Ei[i];
      i++;
    }

    int j = 0;
    while (j < x.length && j < Ei.length) {
        update1 = update1 + (x[j] + Ei[j]) * (Ei[j]/Math.sqrt(update2));
        j++;
    }
    return (Math.sqrt(filePropertiesLocalStore.retrieveDouble("lower.bound.outlier")*T) - (update1) );
  }


  public double phiMinusZeroArgumentColumnFunction(FastAGMS E, int column, double T) {
    double update1 = 0.0;

    int j = 0;
    Double[] Ei = E.getSketchColumn(column);
    while (j < Ei.length) {
      update1 = update1 + (Ei[j]) * (Ei[j]);
      j++;
    }

    int i = 0;
    double update2 = 0.0d;
    while (i < Ei.length) {
      update2 = update2 + (Ei[i]/Math.sqrt(update1)) * (Ei[i]);
      i++;
    }


    return Math.sqrt( filePropertiesLocalStore.retrieveDouble("lower.bound.outlier")*T ) - (update2 ) ;
  }

  public <IMPL extends Vector> double computePhiWithZeroXi(IMPL E, Double T) {
    FastAGMS estimation = (FastAGMS) E;
    int depth = estimation.depth();
    upperBoundBooleanArray = new boolean[depth];
    lowerBoundBooleanArray = new boolean[depth];

    for (int i = 0; i < depth; i++) {
      upperBoundBooleanArray[i] = true;
      lowerBoundBooleanArray[i] = true;
    }

    int p1 = 0; // p1
    int p2 = 0; // p2


    for (int i = 0; i < estimation.depth(); i++) {
      if (innerProduct(estimation.getSketchColumn(i)) >= filePropertiesLocalStore.retrieveDouble("higher.bound.outlier") * T) {
        upperBoundBooleanArray[i] = false;
        p1++;
      }
    }
    tempUpperBoundBooleanArray = new int[estimation.depth() - p1];
    int index1 = 0;
    for (int i = 0; i < estimation.depth(); i++) {
      if (upperBoundBooleanArray[i]) {
        tempUpperBoundBooleanArray[index1] = i;
        index1++;
      }
    }

    for (int i = 0; i < estimation.depth(); i++) {
      if (innerProduct(estimation.getSketchColumn(i)) <= filePropertiesLocalStore.retrieveDouble("lower.bound.outlier") * T) {
        lowerBoundBooleanArray[i] = false;
        p2++;
      }
    }

    tempLowerBoundBooleanArray = new int[estimation.depth() - p2];
    int index2 = 0;
    for (int i = 0; i < estimation.depth(); i++) {
      if (lowerBoundBooleanArray[i]) {
        tempLowerBoundBooleanArray[index2] = i;
        index2 = index2 + 1;
      }
    }

    int majority = majorityNumber(depth); // n depth of sketch

    // find all of the combinations of n - p + 1 - majority of those indexes of booleanArray that

    // are true. (m)

    double maxPhiPlusValue = 0.0;
    boolean started = false;

      Iterator<int[]> iterator1 = CombinatoricsUtils.combinationsIterator(estimation.depth() - p1, estimation.depth() - p1 + 1 - majority);
      while (iterator1.hasNext()) {
        final int[] combination1 = iterator1.next();
        double temp = phiZeroXiPlus(combination1, estimation, T);

        if(!started) {
	        maxPhiPlusValue = temp;
	        started = true;
	      }
	      else {
	        if (maxPhiPlusValue < temp) {
            maxPhiPlusValue = temp;
	        }
	      }
      }

  double maxPhiMinusValue = 0.0;
  boolean started1 = false;

  int m = 0;

    Iterator<int[]> iterator2 = CombinatoricsUtils.combinationsIterator(estimation.depth() - p2, estimation.depth() - p2 + 1 - majority);
    while (iterator2.hasNext()) {
      m = m + 1;
      final int[] combination2 = iterator2.next();
      double temp = phiZeroXiMinus(combination2, estimation, T);
      if(!started1) {
        maxPhiMinusValue = temp;
        started1 = true;
      }
      else {
        if (maxPhiMinusValue < temp) {
          maxPhiMinusValue = temp;
        }
      }
    }

    if (maxPhiMinusValue <= maxPhiPlusValue) {
      return maxPhiPlusValue;
    }
    else {
    return maxPhiMinusValue;
    }
  }
}

