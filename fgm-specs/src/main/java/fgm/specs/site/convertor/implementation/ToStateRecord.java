package fgm.specs.site.convertor.implementation;

import fgm.specs.data.StreamRecord;
import fgm.specs.site.convertor.IToStateVectorConvertor;
import fgm.specs.site.state.StateVectorRecord;

public class ToStateRecord<K> implements IToStateVectorConvertor<K> {
     public <K> K toStateVectorRecord(StreamRecord streamRecord) {
            return (K)streamRecord.getKey();
        }
}
