package fgm.specs.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fgm.specs.common.vector.FastAGMS;
import fgm.specs.common.vector.KeyFrequencyList;
import fgm.specs.data.StreamRecord;


import java.io.Serializable;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FastAGMS.class, name = "FastAGMS"),
        @JsonSubTypes.Type(value = KeyFrequencyList.class, name = "KeyFrequencyList")
})
public interface Vector extends Serializable {
    void scale(Double scalar);
    void update(StreamRecord record, int frequency);
    void add(Vector vector);

    Vector subtract(Vector vector);



}
