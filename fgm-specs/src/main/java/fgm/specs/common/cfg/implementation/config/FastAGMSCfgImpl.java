package fgm.specs.common.cfg.implementation.config;

import fgm.specs.common.cfg.FastAGMSConfiguration;
import fgm.specs.common.Vector;


public class FastAGMSCfgImpl<T extends Vector> extends BaseCfgImpl<T> implements FastAGMSConfiguration<T> {
    private static final String SKETCH_DEPTH = "sketch.depth";
    private static final String SKETCH_BUCKETS = "sketch.buckets";
    public FastAGMSCfgImpl() {
        super();
    }

    public Integer getSketchDepth() {
        return super.filePropertiesLocalStore.retrieveInt(SKETCH_DEPTH);
    }

    public Integer getSketchBuckets() {
        return super.filePropertiesLocalStore.retrieveInt(SKETCH_BUCKETS);
    }



}
