package fgm.specs.common.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import fgm.specs.common.Vector;

import java.io.Serializable;


public class DriftVectorMessage  implements Serializable {
  private static final long serialVersionUID = 3L; //assign a long value
  @JsonProperty
  private Vector stateVector;
  @JsonProperty
  private String siteId;
  @JsonProperty
  private int currentRound;
  @JsonProperty
  private int currentSubround;

  public DriftVectorMessage() {
  }

  public void setStateVector(Vector stateVector) {
    this.stateVector =  stateVector ;
  }
  public Vector getStateVector() {
    return this.stateVector;
  }
  public String getSiteId() {
    return this.siteId;
  }

  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }

  public Integer getCurrentRound() {
    return this.currentRound;
  }

  public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
  public Integer getCurrentSubround() {
    return this.currentSubround;
  }

  public void setCurrentSubround(int currentSubround) { this.currentSubround = currentSubround; }


}
