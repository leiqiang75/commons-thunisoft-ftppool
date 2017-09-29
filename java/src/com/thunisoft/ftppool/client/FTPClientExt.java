package com.thunisoft.ftppool.client;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.MLSxEntryParser;

/**
 * FTPClientExt
 * 继承FTPClient，因为FTPClient的mlistFile方法在封装文件大小(size)
 * 的处理过程有问题，没有空格时也做了截取，导致size的key不正确。
 * @author liuyang
 * @date 2017-9-8
 */
public class FTPClientExt extends FTPClient {
    public FTPClientExt() {
        super();
    }

    /**
     * Get file details using the MLST command
     *
     * @param pathname the file or directory to list, may be {@code null}
     * @return the file details, may be {@code null}
     * @throws IOException on error
     * @since 3.0
     */
    @Override
    public FTPFile mlistFile(String pathname) throws IOException {
        boolean success = FTPReply.isPositiveCompletion(sendCommand(FTPCmd.MLST, pathname));
        if (success){
            String reply = getReplyStrings()[1];
            /* check the response makes sense.
             * Must have space before fact(s) and between fact(s) and filename
             * Fact(s) can be absent, so at least 3 chars are needed.
             */
            if (reply.length() < 3) {
                throw new MalformedServerReplyException("Invalid server reply (MLST): '" + reply + "'");
            }
            String entry = StringUtils.EMPTY;
            //此处为什么不用trim()去空格，而是substring呢？
            if (reply.charAt(0) != ' ') {
                entry = reply;
            } else {
                entry = reply.substring(1); // skip leading space for parser
            }
            return MLSxEntryParser.parseEntry(entry);
        } else {
            return null;
        }
    }
}
