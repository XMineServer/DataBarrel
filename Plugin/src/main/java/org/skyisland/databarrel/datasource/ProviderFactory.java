package org.skyisland.databarrel.datasource;

@FunctionalInterface
public interface ProviderFactory<C, P> {
    P create(C configuration) throws Exception;
}
