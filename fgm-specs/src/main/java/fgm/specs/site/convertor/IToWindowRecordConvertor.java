package fgm.specs.site.convertor;

import fgm.specs.data.StreamRecord;
import fgm.specs.data.WindowRecord;

public interface IToWindowRecordConvertor<IMPL0 extends WindowRecord, IMPL2 extends StreamRecord> {
    <IMPL0, IMPL2> IMPL0 toWindowRecord(IMPL2 streamRecord);
}
