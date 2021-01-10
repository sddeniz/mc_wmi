package com.behsa.sdp.mc_wmi.redis;

import com.behsa.sdp.mc_wmi.common.DsdpAuthentication;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class CoreRedis {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private Gson gson;

    @Value("${token.validation}")
    private int tokenValidate;


    private static final String KEY_PREFIX_DSDP = "api:apiGateway:";
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreRedis.class);


    public Long getRateLimitTpsTpd(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String str = jedis.get(KEY_PREFIX_DSDP + key);
            if (str == null || str.isEmpty()) {
                return null;
            }
            return this.gson.fromJson(str, Long.class);
        } catch (JedisException e) {
            LOGGER.error("getRateLimitTpsTpd with key:{}", key, e);
        }
        return null;
    }

    public void setRateLimitTps(String key, Long value) {
        String redisValues = gson.toJson(value);
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.setex(KEY_PREFIX_DSDP + key, 1, redisValues);
        } catch (JedisException e) {
            LOGGER.error("setRateLimitTps with key:{} , value:{}", key, value, e);

        }
    }

    public void setRateLimitTpd(String key, Long value) {
        String redisValues = gson.toJson(value);
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.setex(KEY_PREFIX_DSDP + key, 86400,redisValues);
         } catch (JedisException e) {
            LOGGER.error("setRateLimitTpd with key:{} , value:{}", key, value, e);
        }
    }

    public void cleanRedis() {
        Set<String> matchingKeys = new HashSet<>();
        ScanParams params = new ScanParams();
        params.match(KEY_PREFIX_DSDP + "*");

        String nextCursor = "0";
        try (Jedis jedis = this.jedisPool.getResource()) {
            do {
                ScanResult<String> scanResult = jedis.scan(nextCursor, params);
                List<String> keys = scanResult.getResult();
                nextCursor = scanResult.getCursor();

                matchingKeys.addAll(keys);

            } while (!nextCursor.equals("0"));
        } catch (JedisException e) {
            e.printStackTrace();
        }

        if (matchingKeys.size() == 0) {
            return;
        }
        matchingKeys.forEach(System.out::println);
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.del(matchingKeys.toArray(new String[matchingKeys.size()]));
        } catch (JedisException e) {
            LOGGER.error("cleanRedis ", e);
        }

    }

    public void setRateLimitTpsTpd(String keyTps, String keyTpd, long tps, long tpd) {
        String redisValuesTps = gson.toJson(tps);
        String redisValuesTpd = gson.toJson(tpd);
        try (Jedis jedis = this.jedisPool.getResource()) {
            Pipeline pipelined = jedis.pipelined();
            pipelined.setex(KEY_PREFIX_DSDP + keyTps, 1, redisValuesTps);
            pipelined.setex(KEY_PREFIX_DSDP + keyTpd, 84600, redisValuesTpd);
        } catch (JedisException e) {
            LOGGER.error("setRateLimitTpsTpd with keyTps:{} , valueTps:{} , keyTpd:{} , valueTpd:{}", keyTps, tps, keyTpd, tpd, e);
        }
    }


    public void setUsage(String key, Long value) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.set(KEY_PREFIX_DSDP + key, value.toString());
        } catch (JedisException e) {
            LOGGER.error("setUsage with key:{} , value:{}", key, value, e);
        }
    }

    public Long getUsage(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String str = jedis.get(KEY_PREFIX_DSDP + key);
            if (str == null || str.isEmpty()) {
                return null;
            }
            return this.gson.fromJson(str, Long.TYPE);
        } catch (JedisException e) {
            LOGGER.error("getUsage with key:{} ", key, e);
        }
        return null;
    }


    public void setAuthentication(String key, DsdpAuthentication authentication) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.setex(KEY_PREFIX_DSDP + key, tokenValidate, gson.toJson(authentication));
        } catch (JedisException e) {
            LOGGER.error("setAuthentication with authentication:{} ", authentication, e);
        }
    }

    public void setAuthenticationSaltToken(String key, DsdpAuthentication servicesAccess) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.setex(KEY_PREFIX_DSDP + key, tokenValidate, gson.toJson(servicesAccess));
        } catch (JedisException e) {
            LOGGER.error("setAuthentication with authentication:{} ", servicesAccess, e);
        }
    }


    public DsdpAuthentication getAuthentication(String token) {
        try (Jedis jedis = jedisPool.getResource()) {
            String str = jedis.get(KEY_PREFIX_DSDP + token);
            if (str == null || str.equals("")) {
                return null;
            }
            return gson.fromJson(str, DsdpAuthentication.class);
        } catch (JedisException e) {
            LOGGER.error("getAuthentication with token:{} ", token, e);
            return null;
        }
    }

    public DsdpAuthentication getAuthenticationServiceToken(String token) {
        try (Jedis jedis = jedisPool.getResource()) {
            String str = jedis.get(KEY_PREFIX_DSDP + token);
            if (str == null || str.equals("")) {
                return null;
            }
            return gson.fromJson(str, DsdpAuthentication.class);
        } catch (JedisException e) {
            LOGGER.error("getAuthentication with token:{} ", token, e);
            return null;
        }
    }


}
