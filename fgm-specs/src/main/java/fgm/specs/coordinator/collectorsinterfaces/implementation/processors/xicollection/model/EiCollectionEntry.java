package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.xicollection.model;

import fgm.specs.common.Vector;

import java.util.AbstractMap;

public class EiCollectionEntry<T extends Vector> {

    private AbstractMap.SimpleEntry<String, T> Ei_Entry; //the string is the node id the FastAGMS is the Local Statistic modified in order to create the global Statistic accoding to the paper
    public EiCollectionEntry(String nodeId,  T nodeLocalStatisticVector) {
        this.Ei_Entry = new AbstractMap.SimpleEntry<>(nodeId, nodeLocalStatisticVector);
    }
    public String getEiNodeId() {
        return this.Ei_Entry.getKey();
    }
    public T getEiLocalStatistic(){
        return this.Ei_Entry.getValue();
    }
}
