package com.behsa.sdp.mc_wmi.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public abstract class CacheRepository<T> implements ICacheRedis<T> {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void setRedis(String token, T object) {
        getCache().put(token, object);
    }

    @Override
    public T getRedis(String token) {
        return null ; //getCache().get(token ,);
    }

    protected abstract String getCacheName();

    @Override
    public boolean remove(String token) {
        getCache().evict(token);
        return true;
    }

    protected Cache getCache() {
        return cacheManager.getCache(getCacheName());
    }


}
