package com.thunisoft.ftppool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thunisoft.ftppool.client.FtpInfo;
import com.thunisoft.ftppool.pool.FtpClientPoolConfig;

public class FtpClientPoolTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void ftpClientPoolTest() {
        FtpClientPoolManager manager = new FtpClientPoolManager();
        FtpClientPoolConfig config = new FtpClientPoolConfig();
        config.setMaxTotalPerKey("500");
        config.setMaxIdlePerKey("100");
        config.setMinIdlePerKey("50");
        FtpClientFactory factory = new FtpClientFactory();
        manager.setConfig(config);
        manager.setFactory(factory);
        FileOutputStream fio = null;
        try {
            fio = new FileOutputStream(new File("D:\\8888.pdf"));

            FtpInfo info = new FtpInfo("172.16.192.139", 21, "VOD", "DOV");
            FTPClient client = manager.getFtpClient(info);
            if (null == client) {
                System.out.println("获取客户端失败！");
            }
            client.changeWorkingDirectory("2400/201706/30100/079C74CA80F0E63FAF7D60F07BAB7B84/archives/");
            client.retrieveFile("82C120F0F42F9C30022257AD25593CF0_pdf.pdf", fio);

            logger.info("文件下载完成");
            logger.info("当前连接数为：" + manager.getPool().getNumActive());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if (fio != null) {
                try {
                    fio.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void ftpClientPoolBatchTest() throws Exception {
        final FtpClientPoolManager manager = new FtpClientPoolManager();
        FtpClientPoolConfig config = new FtpClientPoolConfig();
        //所有连接数，包括pool池中的和其他
        config.setMaxTotalPerKey("500");
        config.setMaxIdlePerKey("50");
        config.setMinIdlePerKey("10");
        FtpClientFactory factory = new FtpClientFactory();
        manager.setConfig(config);
        manager.setFactory(factory);

        final FtpInfo info = new FtpInfo("172.16.192.139", 21, "VOD", "DOV");
        final CountDownLatch counter = new CountDownLatch(20);

        //单元测试，手动初始化ftp连接池
        manager.init();
        //测试等待一定时间，让ftppoo初始化完成
        //        Thread.sleep(20000);

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                final FtpInfo info1 = new FtpInfo("172.16.192.169", 21, "lizhongwei", "6789@jkl");
                new Thread(ftpClientTask(counter, manager, info1)).start();
                if (i % 10 == 0) {
                    Thread.sleep(1000);
                }
                continue;
            }
            new Thread(ftpClientTask(counter, manager, info)).start();
            if (i % 10 == 0) {
                Thread.sleep(10000);
            }
        }
        try {
            counter.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        manager.getPool().close();
        System.out.println("程序结束！");
    }

    private void saveFile2Local(final FtpClientPoolManager manager, final FtpInfo info) {
        FileOutputStream fio = null;
        try {
            fio = new FileOutputStream(new File("E:\\附件\\" + System.currentTimeMillis() + ".pdf"));
            long startTime = System.currentTimeMillis();
            FTPClient client = manager.getFtpClient(info);
            System.out.println("------获取ftpclient链接用时：" + (System.currentTimeMillis() - startTime) + "毫秒");
            System.out.println("当前正在使用的的ftp连接数为：" + manager.getPool().getNumActive());
            //            System.out.println("当前所有被借出的ftp连接数为：" + manager.getPool().getBorrowedCount());
            System.out.println("当前pool中空闲的连接数为：" + manager.getPool().getNumIdle());
            if (null == client) {
                System.out.println("获取客户端失败！");
            }
//            client.changeWorkingDirectory("2400/201706/30100/079C74CA80F0E63FAF7D60F07BAB7B84/archives/");
//            client.retrieveFile("82C120F0F42F9C30022257AD25593CF0_pdf.pdf", fio);
            client.changeWorkingDirectory("2400/201706/30100/079C74CA80F0E63FAF7D60F07BAB7B84/archives");
            client.retrieveFile("C9135095A06EE0D93C94E99FE72580F8.pdf", fio);
            manager.releaseFtpClient(info, client);
            System.out.println("文件下载完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fio != null) {
                try {
                    fio.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Runnable ftpClientTask(final CountDownLatch counter, final FtpClientPoolManager manager, final FtpInfo info) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    saveFile2Local(manager, info);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    counter.countDown();
                }
            }
        };
    }
}
