package com.example.demo.services;



import com.example.demo.entity.FailedSettlement;
import com.example.demo.repository.FailedSettlementRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@ConditionalOnProperty(prefix = "file-watcher", name = "enable", havingValue = "true")
@Slf4j
@Service
public class FileWatcherService {

    private boolean started = false;

    @Value("${file-watcher.host}")
    private String host;

    @Value("${file-watcher.username}")
    private String username;

    @Value("${file-watcher.password}")
    private String password;

    @Value("${file-watcher.remote-dir}")
    private String remoteDir;

    @Value("${file-watcher.enable}")
    private boolean fileWatcherEnabled;
    private List<String> negativeOneLines = new ArrayList<>();

    @Value("${file-watcher.file-pattern}")
    private String filePattern;

    @Autowired
    FailedSettlementRepository failedSettlementRepository;


    @PostConstruct
    void init() {
        while (!started) {
            try {
                String remoteDirectory = remoteDir;
                if (!remoteDir.startsWith("/")) {
                    remoteDirectory = "/" + remoteDir;
                }
                String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
                String encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8);
                log.info("password: " + password);
                String sftpUri = "sftp://" + encodedUsername + ":" + encodedPassword + "@" + host + remoteDirectory;
                watchRemoteDirectory(sftpUri);

               /* watchRemoteDirectory("sftp://"
                    .concat(encodedUsername)
                    .concat(":")
                    .concat(encodedPassword)
                    .concat("@")
                    .concat(host)
                    .concat(remoteDirectory));

                */
                started = true;

                log.info("started to watch T24 remote directory {}" , remoteDir);
            } catch (Exception e) {
                log.error("failed to watch T24 remote directory: " + remoteDir, e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    /* do nothing */
                }
            }
        }
    }

    private void watchRemoteDirectory(String remoteDirectoryUrl) throws FileSystemException {
        log.info("remoteDirectoryUrl {}" , remoteDirectoryUrl);
        FileSystemManager fsManager = VFS.getManager();
        FileObject remoteDir = fsManager.resolveFile(remoteDirectoryUrl);
        log.info("watching remote directory {}" , remoteDir);

        FileListener listener = new FileListener() {

            @Override
            public void fileCreated(FileChangeEvent event) throws Exception {
                log.info("File created {}", filePattern);
                String fileName = event.getFile().getName().getBaseName();
                String datePattern = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String filePattern2 = filePattern.replace("\\\\", "\\");
                Pattern pattern = Pattern.compile(filePattern2);
                Matcher matcher = pattern.matcher(fileName);
                log.info("fileName {} filePattern2 {} filePattern {}", fileName, filePattern2, filePattern);
                if (matcher.find()) {
                    String datePart = matcher.group().replaceAll("\\D", "");
                    if (datePart.equals(datePattern)) {
                        log.info("fileName {} date {}", fileName, datePart);
                        String dateStr = "2022-05-12";

                        // Parsing the string to LocalDate
                        LocalDate localDate = LocalDate.parse(dateStr);
                        readFileContent(event.getFile(),localDate);
                    }
                }
            }
            private static boolean isValidExtension(String fileName) {
                String extensionPattern = ".*\\.(csv|txt)$";
                Pattern pattern = Pattern.compile(extensionPattern, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(fileName);
                return matcher.matches();
            }
            private static void parseFileName(String fileName) {
                // Assuming the filename pattern is "A01-Pelunasan-01-20240712.csv"
                String namePart = fileName.substring(0, fileName.lastIndexOf('.')); // Remove the extension
                String[] parts = namePart.split("-");

                if (parts.length == 4) {
                    String idParameter = parts[0];
                    String transactionType = parts[1];
                    String transactionNumber = parts[2];
                    String date = parts[3];

                    System.out.println("ID Parameter: " + idParameter);
                    System.out.println("Transaction Type: " + transactionType);
                    System.out.println("Transaction Number: " + transactionNumber);
                    System.out.println("Date: " + date);
                } else {
                    System.out.println("Filename format is incorrect.");
                }
            }

            @Override
            public void fileDeleted(FileChangeEvent event) throws Exception {
                // Handle file deletion
            }

            @Override
            public void fileChanged(FileChangeEvent event) throws Exception {
                log.info("File changed {} " , event.getFile().getName().getBaseName());
                // Handle file change
            }
        };

        DefaultFileMonitor fileMonitor = new DefaultFileMonitor(listener);
        fileMonitor.addFile(remoteDir);
        fileMonitor.start();
        log.info("File monitoring started successfully.");

    }

    private void readFileContent(FileObject file, LocalDate appDate) {
        try (InputStream inputStream = file.getContent().getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            Integer i = 0;
            while ((line = reader.readLine()) != null) {
                i++;
                searchRowError(line, i);
            }
            if (!negativeOneLines.isEmpty()) {
                String combinedMessage = String.join("\n", negativeOneLines);
                saveData(combinedMessage, appDate);
            }
            log.info("File successfully processed. Negative one lines: {}", negativeOneLines);
        } catch (Exception e) {
            log.error("Error reading file: " + file.getName().getBaseName(), e);
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
//                log.info(message);
                negativeOneLines.add(message);
            }
        }
    }
    private void saveData(String negativeOneLines, LocalDate appDate) {
        log.info("saving data: {}", negativeOneLines);
        FailedSettlement failedSettlement = new FailedSettlement();
        failedSettlement.setAppDate(appDate);
        failedSettlement.setMessages(negativeOneLines);
        failedSettlementRepository.saveAndFlush(failedSettlement);

    }
}
