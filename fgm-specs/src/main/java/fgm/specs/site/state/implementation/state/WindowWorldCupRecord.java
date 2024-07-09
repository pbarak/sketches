package fgm.specs.site.state.implementation.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fgm.specs.data.WindowRecord;
import fgm.specs.data.implementation.stream.WorldCupRecord;

public class WindowWorldCupRecord extends WorldCupRecord implements WindowRecord {

    @JsonProperty
    private Long windowOrderTimestamp;

    public WindowWorldCupRecord() {
    }
    public WindowWorldCupRecord(WorldCupRecord record){
        super(record.getTimeStamp(),
                record.getClientID(),
                record.getObjectID(),
                record.getSize(),
                record.getMethod(),
                record.getStatus(),
                record.getType(),
                record.getServer());
        this.windowOrderTimestamp = 0L;
    }

    public WindowWorldCupRecord( Long timeStamp,
                                 Long clientID,
                                 Long objectID,
                                 Long size,
                                 Long method,
                                 Long status,
                                 Long type,
                                 Long server,
                                 Long windowOrderTimestamp) {
        super(timeStamp, clientID, objectID, size, method, status, type, server);
        this.windowOrderTimestamp = windowOrderTimestamp;
    }

    @JsonIgnore
    public Long getWindowOrderingKey(){
        return this.windowOrderTimestamp;
    }

    @Override
    @JsonIgnore
    public void setWindowOrderingKey(Long windowOrderTimestamp){
        if(windowOrderTimestamp != null){
            this.windowOrderTimestamp = windowOrderTimestamp;
        }
    }
}

