package fgm.specs.function;

import fgm.specs.common.Vector;

import java.io.Serializable;

public interface SafeFunction<T extends Vector> extends Serializable {
  <IMPL extends Vector> double computePhi(IMPL Xi, IMPL  E, Double T);
  <IMPL extends Vector> double computePhiWithZeroXi(IMPL E, Double T);

  }
