package fgm.specs.site.state;

public class StateVectorRecord<T> {
    private T record;
    public StateVectorRecord(T record) {
        this.record = record;
    }
    public T getStateVectorRecord() {
        return record;
    }
    public void setStateVectorRecord(T record) {
        this.record = record;
    }

}
