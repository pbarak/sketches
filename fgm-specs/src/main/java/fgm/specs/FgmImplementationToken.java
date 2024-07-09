package fgm.specs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fgm.specs.common.vector.FastAGMS;
import fgm.specs.common.vector.KeyFrequencyList;
import fgm.specs.data.implementation.stream.WorldCupRecord;

import java.io.Serializable;

public interface FgmImplementationToken {
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FastAGMS.class, name = "FastAGMS"),
            @JsonSubTypes.Type(value = KeyFrequencyList.class, name = "KeyFrequencyList")
    })
     interface Vector extends Serializable {
        void scale(Double scalar);
        void update(StreamRecord record, int frequency);
        void add(fgm.specs.common.Vector vector);
        Vector subtract(Vector vector);
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "record_type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = WorldCupRecord.class, name = "WorldCupRecord")
    })
    interface StreamRecord extends Serializable {
        <T> T getKey(); // the stream record key
    }
    interface WindowRecord  {
        Long getWindowOrderingKey(); //return the key according to which stream records are collected into the window
        void setWindowOrderingKey(Long timestamp); //override the key according to which stream records are collected into the window
    }

}
