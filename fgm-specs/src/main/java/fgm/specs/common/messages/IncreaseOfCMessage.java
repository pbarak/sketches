package fgm.specs.common.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class IncreaseOfCMessage implements Serializable  {

    private static final long serialVersionUID = 1L;
    @JsonProperty
    private String siteId;
    @JsonProperty
    private Integer currentRound;
    @JsonProperty
    private Integer currentSubround;
    @JsonProperty
    private Integer increaseOfC;


    public IncreaseOfCMessage() {
        this.siteId = new String();
        this.currentRound = 0;
        this.currentSubround = 0;
    }

    public IncreaseOfCMessage(String siteId, Integer currentRound, Integer currentSubround, Integer increaseOfC) {
        this.siteId = siteId;
        this.currentRound = currentRound;
        this.currentSubround = currentSubround;
        this.increaseOfC = increaseOfC;
    }

    public Integer getCurrentRound()
    {
        return this.currentRound;
    }
    public void setCurrentRound(Integer currentRound) { this.currentRound = currentRound; }
    public Integer getCurrentSubround()
    {
        return this.currentSubround;
    }
    public void setCurrentSubround(Integer currentSubround) { this.currentSubround = currentSubround; }
    public void setIncreaseOfCounter(Integer increaseOfC) { this.increaseOfC = increaseOfC; }
    public String getSiteId(){
        return this.siteId;
    }

    public void setSiteId(String siteId){
        this.siteId = siteId;
    }
    @JsonIgnore
    public Integer getIncreaseOfCounter(){
        return this.increaseOfC;
    }
}
