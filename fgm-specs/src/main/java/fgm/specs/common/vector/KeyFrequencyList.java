package fgm.specs.common.vector;

import java.util.*;
import fgm.specs.common.Vector;
import fgm.specs.data.StreamRecord;
import fgm.specs.data.implementation.vector.LongRecord;
import fgm.specs.factory.IFgmFactory;
import fgm.specs.site.convertor.IToStateVectorConvertor;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



public class KeyFrequencyList implements Vector {  //todo add token it will a contract in the factory that this one token and the toStateConverter have token Long
    private ArrayList<AbstractMap.SimpleEntry<LongRecord, Double>> stateVector;
    static Logger logger = Logger.getLogger(KeyFrequencyList.class.getName());
    private IToStateVectorConvertor<?> toStateRecordConvertor;

    public KeyFrequencyList(ArrayList<AbstractMap.SimpleEntry<LongRecord , Double>> stateVector){
        PropertyConfigurator.configure(getClass().getClassLoader().getResourceAsStream("log4j.properties"));
        this.stateVector = stateVector;
    }
    public KeyFrequencyList(IFgmFactory<?> factory) {
        toStateRecordConvertor = factory.toStateVectorRecord();
        PropertyConfigurator.configure(getClass().getClassLoader().getResourceAsStream("log4j.properties"));
        this.stateVector = new ArrayList<>();
    }

    public ArrayList<AbstractMap.SimpleEntry<LongRecord, Double>> getStateVector() {
        return this.stateVector;
    }

    public void setStateVector(ArrayList<AbstractMap.SimpleEntry<LongRecord, Double>> stateVector) {
        this.stateVector = stateVector;
    }

    @Override
    public void update(StreamRecord record, int frequency) {

        //IntegerRecord record1 = (IntegerRecord) record;
        LongRecord record1 = new LongRecord();
        record1.setKey(toStateRecordConvertor.toStateVectorRecord(record));
        record1.setFrequency(1L);
        boolean found = false;
        if(!this.stateVector.isEmpty()) {
            for(int i = 0; i < this.stateVector.size(); i++ ) {
                if(record1.getKey().compareTo( this.stateVector.get(0).getKey().getKey() ) < 0 ) {
                    AbstractMap.SimpleEntry<LongRecord, Double> recordChanged = new AbstractMap.SimpleEntry<LongRecord, Double>(record1, 1.0d);
                    this.stateVector.add(0,  recordChanged);
                }
                if(record1.getKey().compareTo(this.stateVector.get(i).getKey().getKey()) == 0 ) {
                    this.stateVector.set(i, new AbstractMap.SimpleEntry<>(record1, 1.0d + this.stateVector.get(i).getValue()));
                }
                if(i + 1 < this.stateVector.size()) {
                    if(record1.getKey().compareTo(this.stateVector.get(i).getKey().getKey() ) > 0 && record1.getKey().compareTo(this.stateVector.get(i+1).getKey().getKey() ) < 0 ) {
                        AbstractMap.SimpleEntry<LongRecord, Double> recordChanged = new AbstractMap.SimpleEntry<>(record1, 1.0d);
                        this.stateVector.add(i+1,  recordChanged);
                    }
                }
                if(i == this.stateVector.size() - 1) {
                    if(record1.getKey().compareTo( this.stateVector.get(i).getKey().getKey() ) > 0 ) {
                        this.stateVector.add(new AbstractMap.SimpleEntry<>(record1, 1.0d));
                    }
                }
            }
        }
        else {
            this.stateVector.add(new AbstractMap.SimpleEntry<>(record1, 1.0d));
        }
    }

    public void setZero() {
        this.stateVector.clear();
    }

    public void pack() {
        if(!this.stateVector.isEmpty()) {
            for (AbstractMap.SimpleEntry<LongRecord, Double> integerRecord : this.stateVector) {
                if(integerRecord.getValue() == 0) {
                    this.stateVector.remove(integerRecord);
                }
            }
        }
    }


    public void sortStateVector() {
        ArrayList<AbstractMap.SimpleEntry<LongRecord, Double>> newStateVector = new ArrayList<>(this.stateVector);
        if(!newStateVector.isEmpty()) {
            Collections.sort(newStateVector, new Comparator<AbstractMap.SimpleEntry<LongRecord, Double>>() {
                @Override
                public int compare(AbstractMap.SimpleEntry<LongRecord, Double> record1, AbstractMap.SimpleEntry<LongRecord, Double> record2) {
                    if(record1.getKey().getKey().compareTo(record2.getKey().getKey()) < 0) {
                        return -1;
                    }
                    else if(record1.getKey().getKey().compareTo(record2.getKey().getKey()) == 0) {
                        return 0;
                    }
                    else {
                        return 1;
                    }
                }
            });
        }
        this.stateVector = newStateVector;
    }

    public void add(Vector stateVector)
    {
        KeyFrequencyList keyFrequencyList = ((KeyFrequencyList)(stateVector));

        ArrayList<AbstractMap.SimpleEntry<LongRecord, Double>> newStateVector = new ArrayList<>();
        int i = 0, j = 0;
        while(i < this.size() && j < keyFrequencyList.size()) {
            if(this.getStateVector().get(i).getKey().getKey().compareTo(keyFrequencyList.getStateVector().get(j).getKey().getKey()) < 0) {
                newStateVector.add(new AbstractMap.SimpleEntry<>(this.getStateVector().get(i).getKey(),  this.getStateVector().get(i).getValue()));
                i++;
            }
            else if(this.getStateVector().get(i).getKey().getKey().compareTo(keyFrequencyList.getStateVector().get(j).getKey().getKey()) == 0) {
                newStateVector.add(new AbstractMap.SimpleEntry<>(this.getStateVector().get(i).getKey(),  new Double(this.getStateVector().get(i).getValue() + keyFrequencyList.getStateVector().get(j).getValue()) ));
                i++;
                j++;
            }
            else {
                newStateVector.add(new AbstractMap.SimpleEntry<>(keyFrequencyList.getStateVector().get(j).getKey(), keyFrequencyList.getStateVector().get(j).getValue()));
                j++;
            }
        }
        while(i < this.size()) {
            newStateVector.add(new AbstractMap.SimpleEntry<>(this.getStateVector().get(i).getKey(),  this.getStateVector().get(i).getValue()));
            i++;
        }
        while(j < keyFrequencyList.size()) {
            newStateVector.add(new AbstractMap.SimpleEntry<>(keyFrequencyList.getStateVector().get(j).getKey(), keyFrequencyList.getStateVector().get(j).getValue()));
            j++;
        }
        this.stateVector = newStateVector;
    }

    public Vector subtract(Vector stateVector) {
        KeyFrequencyList keyFrequencyList = ((KeyFrequencyList)(stateVector));
        ArrayList<AbstractMap.SimpleEntry<LongRecord, Double>> newStateVector = subtract(this, keyFrequencyList);
        return new KeyFrequencyList(newStateVector);
    }


    private ArrayList<AbstractMap.SimpleEntry<LongRecord, Double>> subtract(KeyFrequencyList stateVector1,  KeyFrequencyList stateVector2) {
        ArrayList<AbstractMap.SimpleEntry<LongRecord, Double>> newStateVector = new ArrayList<>();
        int i = 0;
        int j = 0;
        while(i < stateVector1.size() && j < stateVector2.size()) {
            if(stateVector1.getStateVector().get(i).getKey().getKey().compareTo(stateVector2.getStateVector().get(j).getKey().getKey()) < 0) {
                newStateVector.add(new AbstractMap.SimpleEntry<LongRecord, Double>(stateVector1.getStateVector().get(i).getKey(),  stateVector1.getStateVector().get(i).getValue()));
                i++;
            }
            else if(stateVector1.getStateVector().get(i).getKey().getKey().compareTo(stateVector2.getStateVector().get(j).getKey().getKey()) == 0) {
                if(stateVector1.getStateVector().get(i).getValue() - stateVector2.getStateVector().get(j).getValue() > 0) {
                    newStateVector.add(new AbstractMap.SimpleEntry<LongRecord, Double>(stateVector1.getStateVector().get(i).getKey(),  new Double(stateVector1.getStateVector().get(i).getValue() - stateVector2.getStateVector().get(j).getValue()) ));
                }
                i++;
                j++;
            }
        }
        while(i < stateVector1.size()) {
            newStateVector.add(new AbstractMap.SimpleEntry<LongRecord, Double>(stateVector1.getStateVector().get(i).getKey(),  stateVector1.getStateVector().get(i).getValue()));
            i++;
        }
        return newStateVector;
    }

    public int size() {
        return this.stateVector.size();
    }

    public void scale(Double scalar) {
        for (AbstractMap.SimpleEntry<LongRecord, Double> record : this.stateVector) {
            record.setValue( record.getValue() * scalar);
        }

    }
//
//    public KeyFrequencyList<Record<T>> deepCopy()
//    {
//        //CopyOnWriteArrayList<AbstractMap.SimpleEntry<T, Integer>>  newStateVector = new CopyOnWriteArrayList<AbstractMap.SimpleEntry<T, Integer>>(this.stateVector.toArray());
//        //Iterator it = this.stateVector.iterator();
//        //while(it.hasNext() )
//        //{
//
//        //  AbstractMap.SimpleEntry<T, Integer> record = (AbstractMap.SimpleEntry<T, Integer>) it.next();
//
//        //  if(record == null || record.getKey() == null || record.getValue() == null)
//        //	{
//        //		logger.info("null detected");
//        //	}
//        // else
//        //  {
//        //    newStateVector.add(new AbstractMap.SimpleEntry<T, Integer>(record.getKey(),  record.getValue()));
//        // }
//        //}
//        return new KeyFrequencyList<Record<T>,K>(new ArrayList<AbstractMap.SimpleEntry<T, Integer>>(this.stateVector));
//    }

//    public ArrayList<AbstractMap.SimpleEntry<Record<T>, Integer>> arrayDeepCopy()
//    {
//        return new ArrayList<AbstractMap.SimpleEntry<T, Integer>>(this.stateVector);
//    }

}
