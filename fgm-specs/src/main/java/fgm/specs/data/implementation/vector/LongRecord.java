package fgm.specs.data.implementation.vector;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fgm.specs.data.StreamRecord;

public class LongRecord implements StreamRecord {

    @JsonProperty
    private Long key;

    @JsonProperty
    private Long frequency;

    public LongRecord() {
    }

    public LongRecord(Long key, Long frequency) {
        this.key = key;
        this.frequency = frequency;
    }

    @Override
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

}

