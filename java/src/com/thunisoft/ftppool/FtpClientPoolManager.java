package com.thunisoft.ftppool;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import com.thunisoft.ftppool.client.FtpInfo;
import com.thunisoft.ftppool.pool.FtpClientPool;
import com.thunisoft.ftppool.pool.FtpClientPoolConfig;

/**
 * FtpClientManager
 * FtpClient 连接池管理类，用来初始化和销毁ftp pool，以及对外提供获取和释放ftp连接。
 * @author liuyang
 * @version 1.0
 * @date 2017-09-08
 */
public class FtpClientPoolManager {

    Log logger = LogFactory.getLog(this.getClass());

    private FtpClientPool<FtpInfo, FTPClient> pool;

    private FtpClientPoolConfig config;

    private FtpClientFactory factory;

    /**
     * 获取链接
     * @param info
     * @return
     * @throws Exception
     */
    public FTPClient getFtpClient(FtpInfo info) throws Exception {
        if (this.pool == null) {
            init();
        }
        long startTime = System.currentTimeMillis();
        FTPClient client = pool.borrowObject(info);
        logger.info("------从ftp连接池获取连接【"+info.getHost()+"】成功,用时：" + (System.currentTimeMillis() - startTime) + "毫秒，池中可用连接数量：【" + pool.getNumIdle() + "】个,总数量为：【" + (pool.getNumActive() + pool.getNumIdle()) + "】个");
        return client;
    }

    /**
     * 释放链接
     * @param info
     * @param client
     * @throws IOException 
     */
    public void releaseFtpClient(FtpInfo info, FTPClient client) throws IOException {
        boolean changeSuccess = client.changeWorkingDirectory("/");
        if (!changeSuccess) {
            client.printWorkingDirectory();
            logger.error("");
        }
        pool.returnObject(info, client);
        logger.info("------ftpClient【"+info.getHost()+"】使用完成，释放链接。池中可用连接数量：【" + pool.getNumIdle() + "】个,总数量为：【" + (pool.getNumActive() + pool.getNumIdle()) + "】个");
    }

    /**
     * @Description: 初始化数据
     * @param @throws Exception   
     * @return 
     * @author liuyang
     * @date 2017-9-11
     */
    public void init() throws Exception {
        pool = new FtpClientPool<FtpInfo, FTPClient>(factory, config.buildPoolConfig());
        logger.info("--ftp pool inited");
    }

    /**
     * @Description: 销毁连接池
     * @param @throws Exception   
     * @return 
     * @author liuyang
     * @date 2017-9-11
     */
    public void destroy() throws Exception {
        if (this.pool != null) {
            this.pool.close();
        }
        logger.info("--ftp pool destroy");
    }

    public void setConfig(FtpClientPoolConfig config) {
        this.config = config;
    }

    public void setFactory(FtpClientFactory factory) {
        this.factory = factory;
    }

    public FtpClientPool<FtpInfo, FTPClient> getPool() {
        return pool;
    }
}
