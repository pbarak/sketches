package fgm.specs.site.state.implementation.state;

import fgm.specs.common.Vector;
import fgm.specs.site.state.Statistic;

public abstract class FgmStatistic<T extends Vector> implements Statistic<T> {
    private T stateVector;
    public FgmStatistic (){};
    public FgmStatistic(T vector) {
        this.stateVector = vector;
    }
    public T getVector() {
        return stateVector;
    }
    public <K extends Vector> void setVector(K vector) {
        this.stateVector = (T)vector;
    } //guaranteed to be safe according to paper
    public <K extends Vector> void addVectors(K vector) {
        this.stateVector.add(vector);
    }
    public void scaleVector(Double scalar) {
        this.stateVector.scale(scalar);
    }

}
