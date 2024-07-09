package fgm.specs.common.cfg;

import fgm.specs.common.Vector;
public interface FastAGMSConfiguration<T extends Vector> extends BaseConfiguration<T> {
    Integer getSketchDepth();
    Integer getSketchBuckets();
}
