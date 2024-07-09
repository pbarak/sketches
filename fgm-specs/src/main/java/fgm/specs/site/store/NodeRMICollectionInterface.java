package fgm.specs.site.store;

import fgm.specs.site.state.NodeRMIInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface NodeRMICollectionInterface extends Remote {
   void add(NodeRMIInterface<?> site) throws RemoteException;
   List<NodeRMIInterface<?>> getAll() throws RemoteException;
}
