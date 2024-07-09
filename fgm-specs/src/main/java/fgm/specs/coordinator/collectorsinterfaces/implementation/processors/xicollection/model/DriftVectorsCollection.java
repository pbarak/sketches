package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.xicollection.model;


import fgm.specs.common.messages.DriftVectorMessage;
import java.util.ArrayList;
import java.util.List;

public class DriftVectorsCollection {
    private List<DriftVectorEntry<?>> Xis;
    public DriftVectorsCollection(){
        this.Xis = new ArrayList<>();
    }
    public List<DriftVectorEntry<?>> getXis(){
        if(this.Xis != null) {
            return this.Xis;
        } else {
            this.Xis = new ArrayList<>();
            return Xis;
        }
    }
    public DriftVectorEntry<?> getEntry(int i) {
        return this.getXis().get(i);
    }
    public void collectXi(DriftVectorMessage driftVectorMessage) {
        this.getXis().add(
                new DriftVectorEntry<>(driftVectorMessage.getSiteId(),
                        driftVectorMessage.getStateVector()));
    }
}
