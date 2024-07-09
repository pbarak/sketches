package fgm.specs.data.implementation.vector;

public class Record<T> {
    private T key;
    private Integer frequency;

    public Record() {
    }

    public Record(T key, Integer frequency) {
        this.key = key;
        this.frequency = frequency;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

}
