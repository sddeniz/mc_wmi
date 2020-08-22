package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;


@Component
public class CoreRedis {
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private Gson gson;

    private final String KEY_PREFIX_DSDP = "api:dsdp:";

    /**
     * set key in redis for distributed system
     *
     * @param dsdpKey       key
     * @param permissionDto permissions
     */
    public void insertCacheRedis(String dsdpKey, PermissionDto permissionDto) {

        String redisValues = gson.toJson(permissionDto);
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.set(KEY_PREFIX_DSDP + dsdpKey, redisValues);
        } catch (JedisException e) {
            e.printStackTrace();
        }
    }

    /**
     * redis get info by key
     *
     * @param dsdpKey
     * @return
     */
    public PermissionDto readCacheRedis(String dsdpKey) {

        PermissionDto permissionDto = null;
        try (Jedis jedis = jedisPool.getResource()) {
            String str = jedis.get(KEY_PREFIX_DSDP + dsdpKey);
            if (str != null && !str.equals("")) {
                return this.gson.fromJson(str, PermissionDto.class);
            }

        } catch (JedisException e) {
            e.printStackTrace();
        }
        return null;
    }
}
