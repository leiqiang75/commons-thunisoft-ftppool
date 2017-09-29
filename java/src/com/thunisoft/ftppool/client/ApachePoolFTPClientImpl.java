package com.thunisoft.ftppool.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.thunisoft.ftppool.FtpClientPoolManager;

/**
 * ftp 客户端实现 此类是一个adapter
 * ApachePoolFTPClientImpl 
 * @author liuyang
 * @date 2017-9-8
 */
public class ApachePoolFTPClientImpl implements IFTPClient {

    Log logger = LogFactory.getLog(this.getClass());

    private FtpClientPoolManager ftpClientManager;

    private FtpInfo info;
    
    protected FTPClient client;

    private String encoding = "GBK";
    
    /** 默认值 */
    public long SPACESIZE_DEFAULTVALUE = -1;

    public ApachePoolFTPClientImpl(FTPClient client) {
        super();
        if(client == null){
            throw new IllegalArgumentException("构造参数  FTPClient 不能为空");
        }
        this.client = client;
    }
    
    public ApachePoolFTPClientImpl(FTPClient client, String encoding) {
        super();
        if(client == null){
            throw new IllegalArgumentException("构造参数  FTPClient 不能为空");
        }
        this.encoding = encoding;
        this.client = client;
    }

    public FTPClient getClient() {
        return client;
    }

    public boolean changeDirectory(String path) throws IOException {
        path = path.replaceAll("\\\\", "/");
        if(path.startsWith("/")){
            path = path.substring(1);
        }
        if(!client.changeWorkingDirectory(path)){
            String p[] = path.split("/");
            for(int i=0;i<p.length;i++){
                //A、B两个线程同时进到这个方法。切换文件夹为archives(第一次这个文件夹是不存在的)，切换失败
                if(!client.changeWorkingDirectory(encode(p[i]))){
                    //假如A线程先执行了makeDirectory方法，创建了archives文件夹，返回true， A继续执行changeWorkingDirectory方法，切换成功
                    //那么B在执行makeDirectory时回返回false，原因550 archives: already exists，B直接return false,不在进行切换
                    //这样就会导致文件在上传的时候并不是我们指定的目录，导致文件获取失败
                    if(client.makeDirectory(encode(p[i]))){
                        logger.debug(client.getReplyString());
                    }else{
                        logger.warn(client.getReplyString());
                        //为什么要在这加一个切换目录的操作，因为创建目录失败，有可能是因为并发的情况，目录已经被创建好了，所以创建目录失败+切换失败才能证明有异常，，返回false
                        if (client.changeWorkingDirectory(encode(p[i]))) {
                            logger.warn(client.getReplyString());
                            continue;
                        }
                        return false;
                    }
                    if(client.changeWorkingDirectory(encode(p[i]))){
                        logger.debug(client.getReplyString());
                    }else{
                        logger.warn(client.getReplyString());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean deleteFile(String name) throws IOException {
        return client.deleteFile(encode(name));
    }

    public InputStream read(String name) throws IOException {
        return this.read(name, 0);
    }

    public InputStream read(String name, long offset) throws IOException {

        //设置偏移量
        client.setRestartOffset(offset);
        InputStream is = client.retrieveFileStream(encode(name));
        
        int replyCode = client.getReplyCode();
        /*
         *  replyCode 在此处每种服务器的返回是不同的
         *  1. ServU     : 150 Opening BINARY mode data connection for <fileName> (<fileSize>).
         *  2. IIS6      : 125 Data connection already open; Transfer starting.
         *  3. FileZilla : 150 Connection accepted 
         *  4. apache FTP: 150 File status okay; about to open data connection.
         *  
         */
        
        // 检查状态
        if(FTPReply.isPositivePreliminary(replyCode)) {
            return is;
        }

        // 处理异常状态
        String replyStr = client.getReplyString();
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        String errMsg = "FTP Replay: " + replyStr;
        logger.error(errMsg);
        
        // 文件不存在的时候返回null
        // 550 <fileName>: The system cannot find the file specified.
        if (replyCode == 550) {
            return null;
        }
        
        // 其他情况，抛出异常
        throw new IOException(errMsg);
    }

    public boolean storeFile(InputStream is, String name) throws IOException {
        boolean result = client.storeFile(encode(name), is);
        if(result){
            logger.debug(client.getReplyString());
        }else{
            String replyStr = client.getReplyString();
            if(StringUtils.isNotBlank(replyStr) && replyStr.startsWith("550")){
                logger.error(replyStr + " Get help:http://172.16.16.148:6080/jira/browse/STORAGE-5");
            }else{
                logger.error(replyStr);
            }
        }
        return result;
    }
    
    public OutputStream storeFileStream(String name) throws IOException {
        
        OutputStream os = client.storeFileStream(encode(name));
        
        int replyCode = client.getReplyCode();
        /*
         *  replyCode 在此处每种服务器的返回是不同的
         *  1. ServU     : 150 Opening BINARY mode data connection for <fileName>.
         *  2. IIS6      : 125 Data connection already open; Transfer starting.
         *  3. FileZilla : 150 Connection accepted 
         *  4. apache FTP: 150 File status okay; about to open data connection.
         *  
         *  测试过的这些服务器的返回值均与completePendingCommand()中的说明中 FTPReply.isPositiveIntermediate()对应的code值范围300-400
         *  的判断不相符，应该是写错了，此处判断时采用isPositivePreliminary方法， 对应的code值范围100-200
         */
        
        // 检查状态
        if(!FTPReply.isPositivePreliminary(replyCode)) {
            String replyStr = client.getReplyString();
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            String errMsg = "File transfer failed. FTP Replay: " + replyStr;
            logger.error(errMsg);
            throw new IOException(errMsg);
        }
        
        return os;
    }
    
    public boolean completeTransFile() throws IOException {
        return client.completePendingCommand();
    }

    public long getUsedDiskSpace(String basePath) {
        return calculateUsedDiskSpace(basePath);
    }
    
    protected long calculateUsedDiskSpace(String path) {
        long useSize = 0;
        try {
            FTPFile[] aryFile;
            if (StringUtils.isBlank(path)) {
                aryFile = client.listFiles();
            } else {
                aryFile = client.listFiles(path);
            }
            for (int index=0; index<aryFile.length; index++){
                FTPFile ftpF =  aryFile[index];
                if (ftpF.isFile()) {
                    useSize += ftpF.getSize();
                } else {
                    useSize += calculateUsedDiskSpace(path+"/"+ftpF.getName());
                }
            }
        } catch (IOException e) {
            logger.error(e);
            useSize = SPACESIZE_DEFAULTVALUE;
        }
        return useSize;
    }
    
    protected String encode(String fileName) throws UnsupportedEncodingException {
        return new String(fileName.getBytes(this.encoding), "ISO8859-1");
    }

    public long getFileSize(String filePath) {
        // 使用新接口获取文件大小 apache的ftp支持此接口
        long fileSize = -1;
        try {
            FTPFile file = client.mlistFile(filePath);
            if (file != null) {
                fileSize = file.getSize();
            }
        } catch (IOException e) {
            logger.warn("Can't get file size use mlistFile method.", e);
        } catch (NoSuchMethodError err) {
            logger.error(
                "Can't get file size use mlistFile method, plase check the version of commons-net.jar , make sure greater than 3.0",
                err);
        }
        
        if (fileSize > 0) {
            return fileSize;
        }
        
        // 使用旧接口获取文件大小  IIS支持此接口 ServU不支持
        try {
            FTPFile[] fs = client.listFiles();
            for (FTPFile f : fs) {
                if (filePath.endsWith(f.getName())){
                    return f.getSize();
                }
            }
        } catch (IOException e) {
            logger.warn("Can't get file size use listFiles method.", e);
        }
        
        return fileSize;
    }

    public void close() throws IOException {
        ftpClientManager.releaseFtpClient(info, client);
    }

    public void setFtpClientManager(FtpClientPoolManager ftpClientManager) {
        this.ftpClientManager = ftpClientManager;
    }

    public void setInfo(FtpInfo info) {
        this.info = info;
    }
}
