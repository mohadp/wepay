package com.jumo.tablas.ui.util;

/**
 * Created by Moha on 11/4/15.
 */
public interface CacheManager<KeyType, ValType> {
    public void addToCache(KeyType key, ValType object);
    public ValType retrieveFromCache(KeyType key);
}
