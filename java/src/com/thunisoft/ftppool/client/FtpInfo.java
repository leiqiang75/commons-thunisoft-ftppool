package com.thunisoft.ftppool.client;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * FtpInfo
 * @author liuyang
 * @version 1.0
 * @date 2017-09-08
 */
public final class FtpInfo {

    /** 默认端口 */
    private static final int DEFAULT_FTP_PORT = 21;

    /** 默认 buffer size */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 16;

    private static final int DEF_CONNECT_TIMEOUT = 0;

    private String host;

    private int port = DEFAULT_FTP_PORT;

    private String userName;

    private String passWord;

    private int bufSize = DEFAULT_BUFFER_SIZE;

    private int connectTimeout = DEF_CONNECT_TIMEOUT;

    private String controlEncoding;

    public FtpInfo(String host, int port, String userName, String passWord) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getHost() {
        return host;
    }

    public FtpInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public FtpInfo setPort(int port) {
        this.port = port;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public FtpInfo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassWord() {
        return passWord;
    }

    public FtpInfo setPassWord(String passWord) {
        this.passWord = passWord;
        return this;
    }

    public int getBufSize() {
        return bufSize;
    }

    public FtpInfo setBufSize(int bufSize) {
        this.bufSize = bufSize;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getControlEncoding() {
        return controlEncoding;
    }

    public void setControlEncoding(String controlEncoding) {
        this.controlEncoding = controlEncoding;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()//
                .append(host)//
                .append(userName)//
                .append(controlEncoding)//
                .append(port)//
                .append(passWord)//
                .hashCode();
    }

    /**
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FtpInfo)) {
            return false;
        }
        FtpInfo rhs = (FtpInfo) obj;
        return new EqualsBuilder()//
                .append(this.host, rhs.host)//
                .append(this.userName, rhs.userName)//
                .append(this.controlEncoding, rhs.controlEncoding)//
                .append(this.port, rhs.port)//
                .append(this.passWord, rhs.passWord)//
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append(this.host)//
                .append(this.port)//
                .append(this.controlEncoding)//
                .append(this.userName)//
                .toString();
    }
}
