package fgm.remote.site.store;

import java.rmi.RemoteException;
import java.util.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.site.state.NodeRMIInterface;
import fgm.specs.site.store.NodeRMICollectionInterface;

public class RemoteSiteStore implements NodeRMICollectionInterface {

  private static final List<NodeRMIInterface<?>> remoteObjects = new ArrayList<>();;
  private static final NodeRMICollectionInterface remoteNodeStore = new RemoteSiteStore();
  private final static FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
  public RemoteSiteStore() {
  }
  public void add(NodeRMIInterface<?> site) throws RemoteException {
    remoteObjects.add(site);

  }

  public List<NodeRMIInterface<?>> getAll() throws RemoteException {
    return remoteObjects;
  }

  public static void main(String args[]) {
    try {
      System.setProperty("java.security.policy", String.valueOf(
          RemoteSiteStore.class.getResource("/resources/project.policy")));
      System.setProperty("java.rmi.server.hostname", filePropertiesLocalStore.retrieveString("java.rmi.server.hostname"));
      if ( System.getSecurityManager() == null ) {
        System.setSecurityManager( new SecurityManager() );
      }
      System.setProperty( "sun.rmi.registry.registryFilter", "java.util.ArrayList;" );
      final NodeRMICollectionInterface stub = (NodeRMICollectionInterface) UnicastRemoteObject.exportObject(remoteNodeStore , filePropertiesLocalStore.retrieveInt("remotestore.port"));
      final Registry registry = LocateRegistry.createRegistry(filePropertiesLocalStore.retrieveInt("remotestore.port"));
      registry.bind( filePropertiesLocalStore.retrieveString("remotestore.bind.name"), stub);
      System.out.println("Server ready");
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }
}
