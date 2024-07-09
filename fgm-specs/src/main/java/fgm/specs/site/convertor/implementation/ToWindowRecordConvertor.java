package fgm.specs.site.convertor.implementation;

import fgm.specs.site.convertor.IToWindowRecordConvertor;
import fgm.specs.site.state.implementation.state.WindowWorldCupRecord;
import fgm.specs.data.StreamRecord;
import fgm.specs.data.WindowRecord;
import fgm.specs.data.implementation.stream.WorldCupRecord;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class ToWindowRecordConvertor<IMPL0 extends WindowRecord, IMPL2 extends StreamRecord> implements IToWindowRecordConvertor<WindowRecord, StreamRecord> {

    public <IMPL0, IMPL2> IMPL0 toWindowRecord(IMPL2 streamRecord) {
        WindowWorldCupRecord r = new WindowWorldCupRecord((WorldCupRecord)streamRecord);
        DateTime dt = new DateTime(DateTimeZone.UTC);
        r.setWindowOrderingKey(Long.valueOf(( dt.getMillis() ) / (Long.valueOf(1000)))); //congestion time according to external clock, this line just converts millis to seconds
        return (IMPL0)r;
    }


}
