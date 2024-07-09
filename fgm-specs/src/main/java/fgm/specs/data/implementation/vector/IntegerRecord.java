package fgm.specs.data.implementation.vector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fgm.specs.data.StreamRecord;

public class IntegerRecord implements StreamRecord {

    @JsonProperty
    private Integer key;

    @JsonProperty
    private Integer frequency;

    public IntegerRecord() {
    }

    public IntegerRecord(Integer key, Integer frequency) {
        this.key = key;
        this.frequency = frequency;
    }

    @Override
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

}
