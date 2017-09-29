package com.thunisoft.ftppool.pool;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FtpClientPoolConfig
 * 提供ftp连接池基本信息的初始化配置
 * @author liuyang
 * @version 1.0
 * @date 2017-09-08
 */
public class FtpClientPoolConfig {
    /** logger */
    private static final Logger LOG = LoggerFactory.getLogger(FtpClientPoolConfig.class);

    /**
     * The default value for the {@code maxTotalPerKey} configuration attribute.
     * @see GenericKeyedObjectPool#getMaxTotalPerKey()
     */
    public static final int DEFAULT_MAX_TOTAL_PER_KEY = -1;

    /**
     * The default value for the {@code maxTotal} configuration attribute.
     * @see GenericKeyedObjectPool#getMaxTotal()
     */
    public static final int DEFAULT_MAX_TOTAL = -1;

    /**
     * The default value for the {@code minIdlePerKey} configuration attribute.
     * @see GenericKeyedObjectPool#getMinIdlePerKey()
     */
    public static final int DEFAULT_MIN_IDLE_PER_KEY = 20;

    /**
     * The default value for the {@code minIdlePerKey} configuration attribute.
     * @see GenericKeyedObjectPool#getMaxIdlePerKey()
     */
    public static final int DEFAULT_MAX_IDLE_PER_KEY = 50;

    public static final long DEFAULT_MAX_WAIT_MILLIS = -1L;

    private static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 1000 * 10;

    private String minIdlePerKey;

    private String maxIdlePerKey;

    private String maxTotalPerKey;

    private String maxTotal;

    private String maxWaitMillis;

    private String timeBetweenEvictionRunsMillis;

    public String getMinIdlePerKey() {
        return minIdlePerKey;
    }

    public void setMinIdlePerKey(String minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
    }

    public String getMaxIdlePerKey() {
        return maxIdlePerKey;
    }

    public void setMaxIdlePerKey(String maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    public String getMaxTotalPerKey() {
        return maxTotalPerKey;
    }

    public void setMaxTotalPerKey(String maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }

    public String getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(String maxTotal) {
        this.maxTotal = maxTotal;
    }

    public String getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(String maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public String getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(String timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public GenericKeyedObjectPoolConfig buildPoolConfig() {

        int maxTotal = NumberUtils.toInt(this.getMaxTotal(), DEFAULT_MAX_TOTAL);
        int maxTotalPerKey = NumberUtils.toInt(this.getMaxTotalPerKey(), DEFAULT_MAX_TOTAL_PER_KEY);
        int maxIdlePerKey = NumberUtils.toInt(this.getMaxIdlePerKey(), DEFAULT_MAX_IDLE_PER_KEY);
        int minIdlePerKey = NumberUtils.toInt(this.getMinIdlePerKey(), DEFAULT_MIN_IDLE_PER_KEY);
        Long maxWaitMillis = NumberUtils.toLong(this.getMaxWaitMillis(), DEFAULT_MAX_WAIT_MILLIS);
        Long timeBetweenEvictionRunsMillis = NumberUtils.toLong(this.getTimeBetweenEvictionRunsMillis(),
            DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);

        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);
        config.setMaxTotal(maxTotal);
        config.setMaxTotalPerKey(maxTotalPerKey);
        config.setMaxIdlePerKey(maxIdlePerKey);
        config.setMinIdlePerKey(minIdlePerKey);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        LOG.info("default, maxTotal:{}, maxTotalPerKey:{}, maxIdlePerKey:{}, minIdlePerKey:{}, maxWaitMillis:{}", DEFAULT_MAX_TOTAL,
            DEFAULT_MAX_TOTAL_PER_KEY, DEFAULT_MAX_IDLE_PER_KEY, DEFAULT_MIN_IDLE_PER_KEY, DEFAULT_MAX_WAIT_MILLIS);
        LOG.info(
            "used, maxTotal:{}, maxTotalPerKey:{}, maxIdlePerKey:{}, minIdlePerKey:{}, maxWaitMillis:{}, timeBetweenEvictionRunsMillis:{}",
            maxTotal, maxTotalPerKey, maxIdlePerKey, minIdlePerKey, maxWaitMillis, timeBetweenEvictionRunsMillis);

        return config;
    }

}
