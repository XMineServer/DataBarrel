package org.skyisland.databarrel.datasource;

import java.util.Optional;

public class LazyDataProvider<C, P> {

    private final C configuration;
    private P provider;
    private final ProviderFactory<C, P> producer;

    public LazyDataProvider(C configuration, ProviderFactory<C, P> producer) {
        this.configuration = configuration;
        this.producer = producer;
    }

    public synchronized P getProvider() throws Exception {
        if (provider != null) {
            provider = producer.create(configuration);
        }
        return provider;
    }

    public synchronized Optional<P> safeGetProvider() {
        return Optional.ofNullable(provider);
    }

    public P createProvider() throws Exception {
        return producer.create(configuration);
    }

}
