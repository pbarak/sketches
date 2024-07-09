package fgm.specs.function.implementation.safefunction;

import fgm.specs.common.Vector;
import fgm.specs.common.vector.GenericKeyFrequencyList;
import fgm.specs.common.vector.KeyFrequencyList;
import fgm.specs.factory.implementation.DependencyInjection;
import fgm.specs.function.SafeFunction;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

public class GenericNormSafeFunction  implements SafeFunction<GenericKeyFrequencyList<Integer>> {
    static Logger logger = Logger.getLogger(NormSafeFunction.class.getName());
    public GenericNormSafeFunction(){
    }

    @Override
    public <IMPL extends Vector> double computePhiWithZeroXi(IMPL E, Double T) {

        KeyFrequencyList keyFrequencyList = null;
        try {
            keyFrequencyList = new KeyFrequencyList(DependencyInjection.getFactory());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return computePhi(keyFrequencyList, E, null);
    }
    @Override
    public <IMPL extends Vector> double computePhi(IMPL X, IMPL E, Double T) {
        double update = 0;
        int i =0, j =0;

        while(i < ((KeyFrequencyList)X).size() && j < ((KeyFrequencyList)E).size())
        {
            if(((KeyFrequencyList)X).getStateVector().get(i).getKey().getKey().compareTo(((KeyFrequencyList)E).getStateVector().get(j).getKey().getKey()) == 0 ) {
                update = update +  ((KeyFrequencyList)X).getStateVector().get(i).getValue().doubleValue() * ((KeyFrequencyList)E).getStateVector().get(j).getValue().doubleValue();
                i++;
                j++;
            }
            else if(((KeyFrequencyList)X).getStateVector().get(i).getKey().getKey().compareTo(((KeyFrequencyList)E).getStateVector().get(j).getKey().getKey()) < 0 ) {
                i++;
            }
            else if(((KeyFrequencyList)X).getStateVector().get(i).getKey().getKey().compareTo(((KeyFrequencyList)E).getStateVector().get(j).getKey().getKey()) > 0) {
                j++;
            }

        }

        double update2 = 0;
        i =0;
        while(i < ((KeyFrequencyList)E).size()) {
            update2 = update2 +   ((KeyFrequencyList)E).getStateVector().get(i).getValue().doubleValue() * ((KeyFrequencyList)E).getStateVector().get(i).getValue().doubleValue();
            i++;
        }
        double update3 = 0;

        i = 0;
        j = 0;
        while(i < ((KeyFrequencyList)X).size() && j < ((KeyFrequencyList)E).size() ) {
            if(((KeyFrequencyList)X).getStateVector().get(i).getKey().getKey().compareTo(((KeyFrequencyList)E).getStateVector().get(j).getKey().getKey()) == 0 ) {
                update3 = update3 + (((KeyFrequencyList)X).getStateVector().get(i).getValue().doubleValue() + ((KeyFrequencyList)E).getStateVector().get(j).getValue().doubleValue()) * (((KeyFrequencyList)X).getStateVector().get(i).getValue().doubleValue() + ((KeyFrequencyList)E).getStateVector().get(j).getValue().doubleValue());
                i++;
                j++;
            }
            else if(((KeyFrequencyList)X).getStateVector().get(i).getKey().getKey().compareTo(((KeyFrequencyList)E).getStateVector().get(j).getKey().getKey()) < 0 ) {
                update3 = update3 + (((KeyFrequencyList)X).getStateVector().get(i).getValue().doubleValue() ) * (((KeyFrequencyList)X).getStateVector().get(i).getValue().doubleValue() );
                i++;
            }
            else if(((KeyFrequencyList)X).getStateVector().get(i).getKey().getKey().compareTo(((KeyFrequencyList)E).getStateVector().get(j).getKey().getKey()) > 0) {
                update3 = update3 + (((KeyFrequencyList)E).getStateVector().get(j).getValue().doubleValue() ) * (((KeyFrequencyList)E).getStateVector().get(j).getValue().doubleValue() );
                j++;
            }
        }

        while(j < ((KeyFrequencyList)E).size()) {
            update3 = update3 + (((KeyFrequencyList)E).getStateVector().get(j).getValue().doubleValue() ) * (((KeyFrequencyList)E).getStateVector().get(j).getValue().doubleValue() );
            j++;
        }

        while(i < ((KeyFrequencyList)X).size()) {
            update3 = update3 + (((KeyFrequencyList)X).getStateVector().get(i).getValue().doubleValue() ) * (((KeyFrequencyList)X).getStateVector().get(i).getValue().doubleValue() );
            i++;
        }

        double phi1 = Math.sqrt(update3) - (1 + 0.05)*Math.sqrt(update2);
        double phi2 = -0.05*Math.sqrt(update2) - update/Math.sqrt(update2);

        if(phi1 > phi2) {
            return phi1;
        }
        else {
            return phi2;
        }
    }
}
