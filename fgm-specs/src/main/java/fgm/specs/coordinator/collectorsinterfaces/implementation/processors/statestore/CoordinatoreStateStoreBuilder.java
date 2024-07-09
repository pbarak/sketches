package fgm.specs.coordinator.collectorsinterfaces.implementation.processors.statestore;

import fgm.specs.common.Vector;
import fgm.specs.factory.IFgmFactory;
import org.apache.kafka.streams.state.StoreBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CoordinatoreStateStoreBuilder<T extends Vector> implements StoreBuilder<CoordinatorStateStore<T>> {

    //the state store builder of the coordinator has to implement the StoreBuilder interface
    private String name;
    private Map<String, String> logConfig = new HashMap<>();
    boolean enableCaching = false;
    boolean enableLogging = false;
    private final IFgmFactory<T> factory;
    public CoordinatoreStateStoreBuilder(IFgmFactory<T> factory){
        this.factory = factory;
        this.name = factory.getBaseCfg().getCoordinatorStateStoreName();
    }

    @Override
    public CoordinatorStateStore<T> build(){
        return new CoordinatorStateStore<>(this.name, enableLogging, 1, factory);
    }
    @Override
    public StoreBuilder<CoordinatorStateStore<T>> withCachingEnabled() {
        return this;
    }

    @Override
    public StoreBuilder<CoordinatorStateStore<T>> withCachingDisabled() {
        return this;
    }

    @Override
    public StoreBuilder<CoordinatorStateStore<T>> withLoggingEnabled(final Map<String, String> config) {
        Objects.requireNonNull(config, "config cannot be null");
        enableLogging = true;
        logConfig = config;
        return this;
    }
    @Override
    public StoreBuilder<CoordinatorStateStore<T>> withLoggingDisabled() {
        enableLogging = false;
        logConfig.clear();
        return this;
    }
    @Override
    public Map<String, String> logConfig(){
        return logConfig;
    }

    @Override
    public boolean loggingEnabled(){
        return enableLogging;
    }

    @Override
    public String name(){
        return this.name;
    }

    public IFgmFactory<T> getFactory(){
        return factory;
    }
}
