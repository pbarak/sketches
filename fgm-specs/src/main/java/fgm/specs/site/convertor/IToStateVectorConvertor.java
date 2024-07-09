package fgm.specs.site.convertor;

import fgm.specs.data.StreamRecord;
import fgm.specs.data.WindowRecord;
import fgm.specs.site.state.StateVectorRecord;

public interface IToStateVectorConvertor<K> {
        <K> K toStateVectorRecord(StreamRecord streamRecord);
}

