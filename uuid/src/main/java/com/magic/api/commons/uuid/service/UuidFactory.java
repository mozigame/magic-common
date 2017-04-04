package com.magic.api.commons.uuid.service;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.codis.JedisFactory;
import com.magic.api.commons.uuid.constants.UuidConstants;
import com.magic.api.commons.uuid.constants.UuidKeys;
import com.magic.api.commons.uuid.utils.InitUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UuidFactory
 *
 * @author zj
 * @date 2016/8/4
 */
public class UuidFactory {

    /**
     * 线程池大小 30
     */
    private static final ExecutorService fixedExecutorService = Executors.newFixedThreadPool(3);

    /**
     * 发号器初始值
     */
    private long startNum;

    /**
     * 发号器总数据量
     */
    private int count;

    /**
     * 分片数
     */
    private int shard;

    /**
     * 发号器是否随机取数
     */
    private boolean isRandom;

    /**
     * 发号器步长
     */
    private int step;

    /**
     * 发号器名称，跟redis确保一致性
     */
    private String uuidName;

    /**
     * 按时间维度生成uuid
     */
    private boolean isGenerateByTime;

    /**
     * jedisFactory
     */
    private JedisFactory jedisFactory;

    /**
     * 随机数位数
     */
    private int seedDigit;

    /**
     * 过滤规则
     */
    private int filterLever;

    /**
     * 初始化
     * @param seedDigit 随机数位数
     */
    public UuidFactory(int seedDigit) {
        this.isGenerateByTime = true;
        this.seedDigit = seedDigit;
    }

    /**
     * 初始化
     * @param digit 发号器位数
     * @param step  发号器步长
     * @param uuidName  发号器名称
     * @param jedisFactory  jedis实例
     */
    public UuidFactory(int digit, int step, String uuidName, JedisFactory jedisFactory) {
        this.startNum = Math.round(Math.pow(10, digit - 1));
        this.step = step;
        this.uuidName = uuidName;
        this.jedisFactory = jedisFactory;
        this.isRandom = false;
        this.init(startNum, uuidName, jedisFactory);
    }

    /**
     * 初始化
     * @param startNum 发号器初始号
     * @param count 发号器数据量
     * @param shard 分片数
     * @param uuidName  发号器名称
     * @param jedisFactory jedis实例
     */
    public UuidFactory(long startNum, int count, int shard, String uuidName, JedisFactory jedisFactory) {
        this.startNum = startNum;
        this.count = count;
        this.shard = shard;
        this.uuidName = uuidName;
        this.isRandom = true;
        this.jedisFactory = jedisFactory;
        this.init(startNum, count, shard, uuidName, jedisFactory);
    }

    /**
     * 初始化
     * @param startNum 发号器初始号
     * @param count 发号器数据量
     * @param shard 分片数
     * @param filterLever 过滤规则
     * @param uuidName  发号器名称
     * @param jedisFactory jedis实例
     */
    public UuidFactory(long startNum, int count, int shard, int filterLever, String uuidName, JedisFactory jedisFactory) {
        this.startNum = startNum;
        this.count = count;
        this.shard = shard;
        this.filterLever = filterLever;
        this.uuidName = uuidName;
        this.isRandom = true;
        this.jedisFactory = jedisFactory;
        this.init(startNum, count, shard, uuidName, jedisFactory);
    }

    /**
     * redis 初始化
     * @param startNum 发号器初始值
     * @param shard 分片数
     * @param uuidName  发号器名称
     * @param jedisFactory  jedis实例
     */
    private void init(long startNum, int count, int shard, String uuidName, JedisFactory jedisFactory) {
        ApiLogger.info("uuid init! startNum:" + startNum + ",count:" + count + ",shard:" + shard + ",uuidName:" + uuidName);
        Jedis instance = jedisFactory.getInstance();
        String key = assembleNextKey(uuidName);//下批发号器基数
        try {
            String value = instance.get(key);
            long next = value == null ? 0 : Long.parseLong(value);//
            if (next < startNum){//初始化发号器基数大于下批发号器基数，则初始化发号器数据
                //记录下次发号器待生成的基数
                instance.set(assembleNextKey(uuidName), String.valueOf(startNum + count));
                //初始化 redis 数据
                initRedis(startNum, count, shard, uuidName);
            }
            ApiLogger.info("uuid init success! startNum:" + startNum + ",count:" + count + ",shard:" + shard + ",uuidName:" + uuidName);
        }catch (Exception e){
            ApiLogger.error("init error! startNum:" + startNum + ",count:" + count +  ",shard:" + shard + ",uuidName:" + uuidName + ",errMsg:" + e.getMessage());
        }
    }

    /**
     * redis 初始化
     *
     * @param startNum  发号器初始值
     * @param uuidName  发号器名称
     * @param jedisFactory jedis实例
     */
    private void init(long startNum, String uuidName, JedisFactory jedisFactory) {
        ApiLogger.info("uuid init! startNum:" + startNum + ",uuidName:" + uuidName);
        String key = assembleCurrentKey(uuidName);//当前发号器数字
        try {
            Jedis instance = jedisFactory.getInstance();
            String value = instance.get(key);
            long next = value == null ? 0 : Long.parseLong(value);
            if (next < startNum){//重新初始化发号器基数
                instance.incrBy(key, startNum - next);
            }
        }catch (Exception e){
            ApiLogger.error("init error! startNum:" + startNum + ",uuidName:" + uuidName + ",errMsg:" + e.getMessage());
        }
    }

    /**
     * 初始化Redis数据
     * @param startNum 发号器位数
     * @param count 数据量
     * @param shard 分片数
     * @param uuidName  发号器名称
     */
    private void initRedis(long startNum, int count, int shard, String uuidName) {
        //初始化uuid,已乱序
        List<Long> ids = InitUtil.generateUuids(startNum, count, this.filterLever);
        ApiLogger.info("generate ids size:" + ids.size());

        //组装key
        String prefix = assemblePrefixKey(uuidName);
        String key;
        try {
            Jedis jedis = getJedisInstance();
            Set<String> result = new HashSet<>();
            int avg = ids.size() / shard;//平均数
            int index = 0;//key0-key99
            for (int i = 0; i < ids.size(); i++){
                Long id = ids.get(i);
                if (id == null || id <= 0)continue;
                if ((i + 1) % avg == 0){//如果达到平均数
                    key = prefix + index;
                    String[] unPushIds = new String[result.size()];
                    result.toArray(unPushIds);
                    ApiLogger.info("push uuids to redis! key:" + key + ",size:" + unPushIds.length);
                    jedis.rpush(key, unPushIds);//批量push到redis
                    result.clear();//清空列表
                    index++;
                }else {
                    result.add(String.valueOf(id));
                }
            }
            if (result.size() > 0){
                key = prefix + index;
                String[] unPushIds = new String[result.size()];
                result.toArray(unPushIds);
                ApiLogger.info("push uuids to redis! key:" + key + ",size:" + unPushIds.length);
                jedis.rpush(key, unPushIds);//批量push到redis
            }
            ApiLogger.info("uuid ids forloop finish!");
        }catch (Exception e){
            ApiLogger.error("init redis error！startNum:" + startNum + ",count:" + count + ",shard:" + shard + ",uuidName:" + uuidName + ",errMsg:" + e.getMessage());
        }
    }

    /**
     * 组装uid key, set集合key
     * @return
     */
    private String assemblePrefixKey(String uuidName) {
        return UuidKeys.UUID + uuidName;
    }

    /**
     * 分配ID
     *
     * @return
     */
    public long assignId(){
        return this.doAssign(this.isGenerateByTime, this.isRandom, this.seedDigit, this.step, this.uuidName);
    }

    /**
     * 分配ID
     *
     * @param isGenerateByTime 时间维度
     * @param isRandom  发号器类型
     * @param seedDigit 种子位数
     * @param step  发号器步长
     * @param uuidName  发号器名称
     * @return
     */
    private long doAssign(boolean isGenerateByTime, boolean isRandom, int seedDigit, int step, String uuidName) {
        if (isGenerateByTime){
            return doAssign(seedDigit);
        }
        if(isRandom){//随机取
            return doAssign(uuidName);
        }
        //步长
        return doAssign(step, uuidName);
    }

    /**
     * 时间戳+随机位数
     * @param seedDigit
     * @return
     */
    private long doAssign(int seedDigit) {
        int seed = (int)((1 + new Random().nextDouble()) * Math.pow(10, seedDigit - 1));
        return System.currentTimeMillis() * (long)Math.pow(10, seedDigit) + seed * 1L;
    }

    /**
     * 定步长
     * @param step  发号器步长
     * @param uuidName  发号器名称
     * @return
     */
    private long doAssign(int step, String uuidName) {
        step = step > 1 ? step : 1;
        String key = assembleCurrentKey(uuidName);
        try {
            return jedisFactory.getInstance().incrBy(key, step);
        }catch (Exception e){
            ApiLogger.error("redis incyBy error! step:" + step + ",key:" + key, e);
            return UuidConstants.UUID_NOT_EXISTS;
        }
    }

    /**
     * 组装随机redis key 当前计数器
     * @param uuidName
     * @return
     */
    private String assembleCurrentKey(String uuidName) {
        return UuidKeys.UUID_CURRENT + uuidName;
    }

    /**
     * 随机
     * @param uuidName
     * @return
     */
    private long doAssign(String uuidName) {
        Jedis instance = getJedisInstance();
        if (instance == null){
            //再加上短信通知
            ApiLogger.error("get jedis instance null！");
            return UuidConstants.UUID_NOT_EXISTS;
        }
        long uid = UuidConstants.UUID_NOT_EXISTS;

        //从哪分片随机取
        int seed = new Random().nextInt(this.shard);
        String key = assembleFixEdKey(seed, uuidName);
        String value;
        try {
            value = instance.rpop(key);
            uid = value == null ? 0 : Long.parseLong(value);
        }catch (Exception e){
            ApiLogger.error("get jedis value error！ key:" + key + ",errMsg:" + e.getMessage());
        }
        if (uid > UuidConstants.UUID_NOT_EXISTS){
            return uid;
        }
        //uid没有落入,取下批触发器基数
        initRedisUuidByShard(seed);
        //[0, seed)
        for (int i = 0; i < seed; i++){
            key = assembleFixEdKey(i, uuidName);
            try{
                value = instance.rpop(key);
                uid = value == null ? 0 : Long.parseLong(value);
                if (uid > UuidConstants.UUID_NOT_EXISTS){
                    return uid;
                }
                initRedisUuidByShard(i);
            }catch (Exception e){
                ApiLogger.error("get jedis value error！ key:" + key + ",errMsg:" + e.getMessage());
            }
        }
        //[seed, shard)
        for (int i = seed + 1; i < this.shard; i++){
            key = assembleFixEdKey(i, uuidName);
            try{
                value = instance.rpop(key);
                uid = value == null ? 0 : Long.parseLong(value);
                if (uid > UuidConstants.UUID_NOT_EXISTS){
                    return uid;
                }
                initRedisUuidByShard(i);
            }catch (Exception e){
                ApiLogger.error("get jedis value error！ key:" + key + ",errMsg:" + e.getMessage());
            }
        }
        if (uid > UuidConstants.UUID_NOT_EXISTS){
            return uid;
        }
        //再从seed进行获取
        try {
            Thread.sleep(200);//休眠200毫秒，确保分片已落数据
            key = assembleFixEdKey(seed, uuidName);
            value = instance.rpop(key);
            uid = value == null ? 0 : Long.parseLong(value);
        }catch (Exception e){
            ApiLogger.error("get jedis value error！ key:" + key + ",errMsg:" + e.getMessage());
        }
        return uid;
    }


    /**
     * 初始化分片数据
     *
     * @param cShard
     */
    private void initRedisUuidByShard(int cShard){
        fixedExecutorService.submit(()-> {
            try{
                ApiLogger.info("init redis shard data ! uuidName:" + uuidName + ",shard:" + cShard);
                Jedis jedis = getJedisInstance();
                if (jedis == null)return;
                String key = assembleNextKey(uuidName);
                long start = jedis.incrBy(key, count / shard);
                if (start <= startNum){
                    start = jedis.incrBy(key, startNum);
                }
                List<Long> ids = InitUtil.generateUuids(start, count / shard, this.filterLever);
                ApiLogger.info("executor ids:" + ids.size());
                key = assembleFixEdKey(cShard, uuidName);
                //同步往redis保存数据
                List<String> values = new ArrayList<>();
                for (int i = 0; i < ids.size(); i++) {
                    values.add(ids.get(i) + "");
                    if ((i + 1) % 100 == 0){//批量 100
                        String[] strings = new String[values.size()];
                        values.toArray(strings);
                        try {
                            jedis.rpush(key, strings);
                            values = new ArrayList<>();
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                ApiLogger.error("sleep thread error! errMsg:" + e.getMessage());
                            }
                        }catch (JedisConnectionException e){
                            ApiLogger.error("redis connect exception! errMsg:" + e.getMessage());
                        }
                    }
                }
                if (!values.isEmpty() && values.size() > 0){//发送剩余的redis指令
                    String[] strings = new String[values.size()];
                    values.toArray(strings);
                    jedis.rpush(key, strings);
                }
            }catch (Exception e){
                ApiLogger.error("init redis shard data error!shard:" + shard + ",count:" + count + ",errMsg:" + e.getMessage());
            }
        });
    }

    /**
     * 组装uid key
     * @param seed
     * @param uuidName
     * @return
     */
    private String assembleFixEdKey(int seed, String uuidName) {
        return UuidKeys.UUID + uuidName + seed;
    }

    /**
     * 组装uid key
     * @return
     */
    private String assembleNextKey(String uuidName) {
        return UuidKeys.UUID_NEXT + uuidName;
    }

    /**
     * 返回jedis实例
     * @return
     */
    public Jedis getJedisInstance() {
        Jedis instance = jedisFactory.getInstance();
        try {
            instance = jedisFactory.getInstance();
        }catch (Exception e){
            try {
                Thread.sleep(200);
                instance = jedisFactory.getInstance();
            }catch (Exception e1){
                try {
                    Thread.sleep(200);
                    instance = jedisFactory.getInstance();
                }catch (Exception e2){
                    ApiLogger.error("get jedis instance error! errMsg:" + e2.getMessage());
                }
            }
        }
        return instance;
    }
}
