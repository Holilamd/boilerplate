package com.example.demo.utills;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class SftpUtil {

    public static ChannelSftp setupJsch(String host, int port, String user, String password) throws Exception {

        JSch jsch = new JSch();
        //create session SFTP
        Session jschSession = jsch.getSession(user, host, port);
        jschSession.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no"); //nonaktif check hostKey
        jschSession.setConfig(config);
        //connect
        jschSession.connect();
        //open SFTP channel
        return (ChannelSftp) jschSession.openChannel("sftp");
    }

    public static InputStream getFileStream(ChannelSftp channelSftp, String remoteFile) throws Exception {
        SftpATTRS attrs;
        try {
            attrs = channelSftp.lstat(remoteFile);
        } catch (SftpException e) {
            throw new Exception("File not found or inaccessible: " + remoteFile, e);
        }
        //Checks if file size is 0 (empty file)
        if (attrs.getSize() == 0) {
            throw new Exception("Empty file: " + remoteFile);
        }
        InputStream inputStream = channelSftp.get(remoteFile);
        return inputStream;
    }
    public static void moveFile(ChannelSftp channelSftp, String remoteFilePath, String destinationFolder,String fileName, String newExtension) throws SftpException {
        String newFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "." + newExtension;
        String sourcePath = remoteFilePath+"/"+fileName;
        String destinationPath = destinationFolder + "/" + newFileName;
        channelSftp.rename(sourcePath, destinationPath);
    }
}
