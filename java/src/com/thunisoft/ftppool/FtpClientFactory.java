package com.thunisoft.ftppool;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thunisoft.ftppool.client.FTPClientExt;
import com.thunisoft.ftppool.client.FtpInfo;


/**
 * FtpClientFactory
 * FTPClient工厂类
 * @author liuyang
 * @version 1.0
 * @date 2017-09-08
 */
public class FtpClientFactory extends BaseKeyedPooledObjectFactory<FtpInfo, FTPClient> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private AtomicLong createCount = new AtomicLong(0);

    @Override
    public FTPClient create(FtpInfo ftpInfo) throws Exception {
        FTPClient client = new FTPClientExt();
        try {
            long startTime = System.currentTimeMillis();

            client.setDefaultPort(ftpInfo.getPort());
            client.connect(ftpInfo.getHost(), ftpInfo.getPort());

            if (!client.login(ftpInfo.getUserName(), ftpInfo.getPassWord())) {
                String replyStr = client.getReplyString();
                String erroe = "Could not login to server. username=" + ftpInfo.getUserName() + ". replyStr="
                        + replyStr;
                client.logout();
                throw new IOException(erroe);
            }

            //检测登录是否成功  
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                String replyStr = client.getReplyString();
                client.disconnect();
                String error = "Can not connect to ftp server . [ip:" + ftpInfo.getHost() + ",prot:"
                        + ftpInfo.getPort() + "]." + replyStr;
                throw new IOException(error);
            }
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.enterLocalPassiveMode();
            client.setBufferSize(ftpInfo.getBufSize());
            createCount.incrementAndGet();
            logger.info("ftp【" + ftpInfo.getHost() + "】链接创建成功，用时：" + (System.currentTimeMillis() - startTime)
                    + "毫秒，ftppool 一共创建：" + createCount.get() + "个链接");
            return client;
        } catch (IOException ex) {
            closeCon(client);
            throw ex;
        }
    }

    /** 
     * TODO 借出归还对象的时候要正确处理ftp的状态，避免disconnet logout 等等重复归还链接导致连接状态异常
     */
    @Override
    public void activateObject(FtpInfo info, PooledObject<FTPClient> pool) throws Exception {
        super.activateObject(info, pool);
    }

    @Override
    public boolean validateObject(FtpInfo key, PooledObject<FTPClient> pool) {
        try {
            FTPClient client = pool.getObject();
            //向服务器发送请求，验证连接是否正常
            boolean validate = client.sendNoOp();
            if (validate) {
                return client.printWorkingDirectory() != null;
            }
            return false;
        } catch (Exception e) {
            logger.warn("{}, msg:{}", key, e.getMessage());
            return false;
        }
    }

    @Override
    public void destroyObject(FtpInfo info, PooledObject<FTPClient> p) throws Exception {
        closeCon(p.getObject());
        logger.info("------ftp【" + info.getHost() + "】链接销毁成功！");
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<FTPClient>(ftpClient);
    }

    /**
     * 销毁ftp连接
     * @param ftpClient
     */
    private void closeCon(FTPClient ftpClient) {

        if (ftpClient == null) {
            return;
        }
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
