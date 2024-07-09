package fgm.specs.common.messages;

import fgm.specs.common.Vector;

//create a generic class Drift  with a generic type T
public class Drift<T extends Vector> {

    private T drift;
    private String siteId;

    public Drift() {
    }
    public Drift(T drift, String siteId) {
        this.drift = drift;
        this.siteId = siteId;
    }
    public T getDrift() {
        return drift;
    }
    public void setDrift(T drift) {
        this.drift = drift;
    }
    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

}
