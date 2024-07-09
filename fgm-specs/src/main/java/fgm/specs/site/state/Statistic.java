package fgm.specs.site.state;

import fgm.specs.common.Vector;

import java.io.Serializable;

public interface Statistic<T extends Vector> extends Serializable {
    T getVector();
    <K extends Vector> void setVector(K vector);
    <K extends Vector> void addVectors(K vector);
    void scaleVector(Double scalar);

}
