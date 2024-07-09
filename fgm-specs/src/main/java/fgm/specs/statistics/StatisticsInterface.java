package fgm.specs.statistics;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StatisticsInterface extends Serializable, Remote {

  void roundEnded(Statistics statistics) throws RemoteException; //the calls are executed from the commands in the command queue!!

  void subroundEnded(Statistics statistics) throws RemoteException; //the calls are executed from the commands in the command queue!!

  void roundStarted(Statistics statistics) throws RemoteException; //the calls are executed from the commands in the command queue!!

  void subroundStarted(Statistics statistics) throws RemoteException; //the calls are executed from the commands in the command queue!!

}
