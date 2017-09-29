package com.thunisoft.ftppool.pool;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * FtpClient连接池核心类，继承GenericKeyedObjectPool，为FIFO行为实现的对象池
 * 此连接池类似于一个map，根据不同的key(FtpInfo)可以初始化多个连接池。
 * @author liuyang
 * @version 1.0
 * @date 2017-09-08
 */
public class FtpClientPool<FtpInfo, FTPClient> extends GenericKeyedObjectPool<FtpInfo, FTPClient> {

    public FtpClientPool(KeyedPooledObjectFactory<FtpInfo, FTPClient> factory, GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }

    public FtpClientPool(KeyedPooledObjectFactory<FtpInfo, FTPClient> factory) {
        this(factory, new GenericKeyedObjectPoolConfig());
    }

}
