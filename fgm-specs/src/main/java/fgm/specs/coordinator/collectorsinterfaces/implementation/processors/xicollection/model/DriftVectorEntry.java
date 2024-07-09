package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.xicollection.model;

import fgm.specs.common.Vector;

import java.util.AbstractMap;

public class DriftVectorEntry<T extends Vector> {
    private AbstractMap.SimpleEntry<String, T> xiEntry; //the string is the node id the FastAGMS is the Node DrifVector collected from the Coordinator
    public DriftVectorEntry(String nodeId,  T nodeDriftVector){
       this.xiEntry = new AbstractMap.SimpleEntry<>(nodeId, nodeDriftVector);
    }
    public String getXiNodeId() {
        return this.xiEntry.getKey();
    }
    public T getXiNodeDriftVector(){
        return this.xiEntry.getValue();
    }
}
