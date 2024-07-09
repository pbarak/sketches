package fgm.specs.site.state;// Write interface for MyCustomStore

import fgm.specs.common.Vector;

import java.rmi.Remote;
import java.rmi.RemoteException;
import fgm.specs.common.GlobalStatistic;

public interface NodeRMIInterface<T extends Vector> extends Remote {

     <IMPL extends Vector> void initializeFgm(GlobalStatistic<IMPL> E, double theta) throws RemoteException;

     <IMPL extends Vector> void startNewRound(GlobalStatistic<IMPL> E, double theta) throws RemoteException;

     void startNewSubround(double theta) throws RemoteException;

     void subroundEnded() throws RemoteException;

     void roundEnded() throws RemoteException;


}


