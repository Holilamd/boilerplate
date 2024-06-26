package com.example.demo.services;

import com.example.demo.dto.SftpRequest;
import com.example.demo.utills.SftpUtil;
import com.jcraft.jsch.ChannelSftp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class FileProcessingService {

    private List<String> negativeOneLines = new ArrayList<>();

    public void processFileFromSftp(SftpRequest req) {

        String remoteFile = req.getPathSource() + "/" + req.getFile();
        try {
            ChannelSftp channelSftp = SftpUtil.setupJsch(req.getHost(), req.getPort(), req.getUser(), req.getPassword());
            channelSftp.connect();

            InputStream inputStream = SftpUtil.getFileStream(channelSftp, remoteFile);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                Integer i = 0;
                while ((line = reader.readLine()) != null) {
                    i++;
                    searchRowError(line, i);
                }
                if (req.isMoveFile()){
                    SftpUtil.moveFile(channelSftp,req.getPathSource(),req.getPathDestination(), req.getFile(), req.getNewExtension());
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            } finally {
                channelSftp.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     private void searchRowError(String line, int i) {
        Pattern pattern = Pattern.compile("\\^(1|\\-1)(.*)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String matchType = matcher.group(1); // This will be "1" or "-1"
            String restOfLine = matcher.group(2); // This will be the rest of the line after ^-1

            if ("-1".equals(matchType)) {
                // Process ^-1
                String message = "Error Row "+i+": " + restOfLine;
                log.info(message);
                negativeOneLines.add(message);
            }
        }
    }
}
