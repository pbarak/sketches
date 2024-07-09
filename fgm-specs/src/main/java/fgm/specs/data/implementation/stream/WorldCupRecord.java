package fgm.specs.data.implementation.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fgm.specs.data.StreamRecord;

public class WorldCupRecord implements StreamRecord {
    private static final int CASHREGISTERFREQUENCY = 1;

    public WorldCupRecord() {
    }

    public WorldCupRecord( Long timeStamp,
                           Long clientID,
                           Long objectID,
                           Long size,
                           Long method,
                           Long status,
                           Long type,
                           Long server) {

        this.timeStamp = timeStamp;
        this.clientID = clientID;
        this.objectID = objectID;
        this.size = size;
        this.method = method;
        this.status = status;
        this.type = type;
        this.server = server;

    }

    @JsonProperty
    private Long timeStamp;
    @JsonProperty
    private Long clientID;
    @JsonProperty
    private Long objectID;
    @JsonProperty
    private Long size;
    @JsonProperty
    private Long method;
    @JsonProperty
    private Long status;
    @JsonProperty
    private Long type;
    @JsonProperty
    private Long server;


    public Long getTimeStamp() {
        Long valueOf = Long.valueOf(this.timeStamp);
        final Long valueOf1 = valueOf;
        return valueOf1;
    }

    public Long getClientID() {
        return this.clientID;
    }
    public void setClientID(Long clientID) {
        this.clientID = clientID;
    }
    public Long getObjectID() {
        return this.objectID;
    }
    public void setObjectID(Long objectID) {
        this.objectID = objectID;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
    public Long getMethod() {
        return this.method;
    }
    public void setMethod(Long method) {
        this.method = method;
    }
    public Long getStatus() {
        return this.status;
    }
    public void setStatus (Long status) {
        this.status = status;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getServer() {
        return this.server;
    }

    public void setServer(Long server) {
        this.server = server;
    }

    @Override
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public Long getKey(){
        return this.clientID;
    }

//    @JsonIgnore
//    public Long getWindowOrderingKey(){
//        return this.timeStamp;
//    }
//
//    public void setWindowOrderingKey(Long windowOrderingKey){
//        if(windowOrderingKey != null){
//            this.timeStamp = windowOrderingKey;
//        }
//    }
}
