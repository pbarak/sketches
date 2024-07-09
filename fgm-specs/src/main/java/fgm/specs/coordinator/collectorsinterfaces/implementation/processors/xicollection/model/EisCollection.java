package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.xicollection.model;

import java.util.ArrayList;
import java.util.List;

public class EisCollection {

    //the coordinator maintains, for each site i, an estimated state vector Ei, according to paper.
    private List<EiCollectionEntry<?>> Eis;
    public EisCollection(){
        this.Eis = new ArrayList<>();
    }
    public EiCollectionEntry<?> getEntry(int i) {
        return this.getEis().get(i);
    }
    public List<EiCollectionEntry<?>> getEis(){
        if(this.Eis != null) {
            return this.Eis;
        } else {
            return new ArrayList<>();
        }
    }
    public String getEiNodeId (int i) {
        if (this.Eis != null) {
            return this.getEis().get(i).getEiNodeId();
        }
        return null;
    }
    public void collectEi(DriftVectorEntry<?> driftVectorEntry) {
        this.getEis().add(
                new EiCollectionEntry<>(driftVectorEntry.getXiNodeId(), driftVectorEntry.getXiNodeDriftVector()));
    }

}

