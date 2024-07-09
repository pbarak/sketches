package fgm.specs.data;

public interface WindowRecord extends StreamRecord{
    Long getWindowOrderingKey(); //return the key according to which stream records are collected into the window
    void setWindowOrderingKey(Long timestamp); //override the key according to which stream records are collected into the window
}
