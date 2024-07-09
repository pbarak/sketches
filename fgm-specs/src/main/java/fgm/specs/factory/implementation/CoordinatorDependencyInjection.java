package fgm.specs.factory.implementation;

import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.coordinator.collectorsinterfaces.implementation.processors.statestore.CoordinatoreStateStoreBuilder;
import fgm.specs.common.Vector;
import fgm.specs.factory.IFgmFactory;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

public class CoordinatorDependencyInjection {
    private final static String factoryPropertyKey = "factory.name"; //corner case of loading properties from property file
    private final static FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
    public static <T extends Vector> CoordinatoreStateStoreBuilder<T> createGenericCoordinatorClient(IFgmFactory<T> factory) {
        return new CoordinatoreStateStoreBuilder<>(factory);
    }
    public static IFgmFactory<?> createFactory(String factoryName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (IFgmFactory<?>)
                Class.forName(factoryName).getDeclaredConstructor().newInstance();
    }
    public static CoordinatoreStateStoreBuilder<?> init(String factoryName) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, RemoteException {
        return createGenericCoordinatorClient(createFactory(factoryName));
    }

    public static IFgmFactory<?> getFactory() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return createFactory(filePropertiesLocalStore.retrieveString(factoryPropertyKey));
    }

}