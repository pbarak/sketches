package fgm.specs.factory.implementation;

import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.site.state.implementation.state.FgmNodeImpl;
import fgm.specs.common.Vector;
import fgm.specs.factory.IFgmFactory;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

public class DependencyInjection {
    private final static String factoryPropertyKey = "factory.name"; //corner case of loading properties from property file
    private final static FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
    public static IFgmFactory<?> getFactory() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return createFactory(filePropertiesLocalStore.retrieveString(factoryPropertyKey));
    }
    public static <T extends Vector> FgmNodeImpl<T> createGenericClient(IFgmFactory<T> factory) throws RemoteException {
        return new FgmNodeImpl<>(factory);
    }

    public static IFgmFactory<?> createFactory(String factoryName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (IFgmFactory<?>)
                Class.forName(factoryName).getDeclaredConstructor().newInstance();
    }

    public static FgmNodeImpl<?> init(String factoryName) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, RemoteException {
        return createGenericClient(createFactory(factoryName));
    }

}
