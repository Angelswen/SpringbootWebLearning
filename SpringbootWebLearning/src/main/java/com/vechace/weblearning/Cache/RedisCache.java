package com.vechace.weblearning.Cache;

import com.vechace.weblearning.Utils.ApplicationContextHolder;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Redis缓存：实现Mybatis的Cache接口，并在ProductMapper.xml中开启二级缓存
 * Created by vechace on 2018/6/23
 */
public class RedisCache implements Cache {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RedisCache.class);

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final String id;
    private RedisTemplate redisTemplate;

    private static final long EXPIRE_TIME_IN_MINUTES = 30;
    public RedisCache(String id){
        if(id ==null){
            throw new IllegalArgumentException("Cache instance require an ID");
        }
        this.id = id;
    }

    @Override
    public String getId(){
        return id;
    }

    /**
     * Put query result to redis Cache
     * @param key
     * @param value
     */
    @Override
    public void putObject(Object key,Object value){
        RedisTemplate redisTemplate =getRedisTemplate();
        ValueOperations opsForValue = redisTemplate.opsForValue();
        opsForValue.set(key,value,EXPIRE_TIME_IN_MINUTES,TimeUnit.MINUTES);
        logger.debug("Put query result to redis ");

    }

    /**
     * Get cached query reslt from redis
     * @param key
     * @return
     */
    @Override
    public Object getObject(Object key){
        RedisTemplate redisTemplate = getRedisTemplate();
        ValueOperations opsForValue = redisTemplate.opsForValue();
        logger.debug("Get cached query result from redis ");
        return opsForValue.get(key);
    }

    /**
     * Remove cached query result from redis
     * @param key
     * @return
     */
    @Override
    public Object removeObject(Object key){

        RedisTemplate redisTemplate = getRedisTemplate();
        redisTemplate.delete(key);
        logger.debug("Remove cached query result from redis");
        return null;

    }

    /**
     * Clear this cache instance
     */
    @Override
    public void clear(){
        RedisTemplate redisTemplate = getRedisTemplate();
        redisTemplate.execute((RedisCallback) connection ->{
            connection.flushDb();
            return null;

        });
        logger.debug("Clear all the cached query from redis");
    }

    /**
     * this method is not used
     * @return
     */
    @Override
    public int getSize(){
        return 0;
    }

    /**
     * Get the ReadWriteLock, this method is also not used
     * @return
     */
    @Override
    public ReadWriteLock getReadWriteLock(){
        return readWriteLock;
    }

    /**
     * Get redisCache bean from ApplicationContext.
     * Because redisTemplate can not use @Autowire to get bean from Spirng
     * @return
     */
    private RedisTemplate getRedisTemplate(){
        if(redisTemplate ==null){
            redisTemplate = ApplicationContextHolder.getBean("redisTemplate");
        }
        return redisTemplate;
    }

}
