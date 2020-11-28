package com.behsa.sdp.mc_wmi.cache;

public interface ICacheRedis<T> {
    T getRedis(String key);

    void setRedis(String key, T object);



    boolean remove(String token);

}
