package com.thunisoft.ftppool.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Title: IFTPClient
 * @Description: FTP客户端接口，封装了客户端的基本操作
 * @author liuyang
 * @date 2017-9-8
 */
public interface IFTPClient {
    
    /**
     * 改变路径
     * @param path
     * @return
     * @throws IOException
     */
    boolean changeDirectory(String path) throws IOException;
    
    /**
     * 删除文件
     * @param name
     * @return
     * @throws IOException
     */
    boolean deleteFile(String name) throws IOException;
    
    /**
     * 保存文件
     * @param is 本地文件流
     * @param name 文件名
     * @return
     * @throws IOException
     * @{@link Deprecated}
     * @see #storeFileStream(String)
     */
    boolean storeFile(InputStream is,String name) throws IOException;

    /**
     * 获取文件的输出流用于上传文件，注意传输完成后需要调用相关的方法完成传输，否则可能导致文件上传不上去
     * {@link #completeTransFile()} 
     * @param name
     * @return
     * @throws IOException
     * 
     */
    public OutputStream storeFileStream(String name) throws IOException;
    
    /**
     * 当使用 {@link #storeFileStream(String)} 的时候需要调用此方法完成传输
     * @return
     * @throws IOException
     */
    public boolean completeTransFile() throws IOException;
    
    /**
     * 读取文件流
     * @param name
     * @return
     * @throws IOException
     */
    InputStream read(String name) throws IOException;
    
    /**
     * 读取文件流
     * @param name
     * @param offset  The offset into the remote file at which to start the next file transfer.  
     *          This must be a value greater than or equal to zero.
     * @return
     * @throws IOException
     */
    InputStream read(String name, long offset) throws IOException;

    /**
     * 关闭客户端
     * @throws IOException 
     */
    void close() throws IOException;
    
    /**
     * 获取FTP存储库使用空间，单位：字节（B）
     * @param basePath ftp基本路径
     * @return
     */
    long getUsedDiskSpace(String basePath);
    
    /**
     * 获取指定文件的大小单位字节（byte）
     * @param filePath
     * @return
     */
    long getFileSize(String filePath);
    

}
