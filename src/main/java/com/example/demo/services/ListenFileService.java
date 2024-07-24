package com.example.demo.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class ListenFileService {
    private boolean started = false;

    @PostConstruct
    void init() throws FileSystemException {
//        while (!started) {
//            String directoryPath = "D:\\Deploy";
//            if (isDirectoryExists(directoryPath)) {
//                watchRemoteDirectory(directoryPath);
//                started = true;
//            } else {
//                log.error("Directory does not exist: {}", directoryPath);
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e1) {
//                    /* do nothing */
//                }
//            }
//        }
    }
    private boolean isDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.exists() && directory.isDirectory();
    }

    private void watchRemoteDirectory(String remoteDirectoryUrl) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();
        FileObject remoteDir = fsManager.resolveFile(remoteDirectoryUrl);
        log.info("watching remote directory2 {}" , remoteDir);

        FileListener listener = new FileListener() {

            @Override
            public void fileCreated(FileChangeEvent event) throws Exception {
                log.info("File created 2 {} " , event.getFile().getName().getBaseName());
            }

            @Override
            public void fileDeleted(FileChangeEvent event) throws Exception {
                log.info("deleted 2 {} " , event.getFile().getName().getBaseName());
                // Handle file deletion
            }

            @Override
            public void fileChanged(FileChangeEvent event) throws Exception {
                log.info("File changed 2{} " , event.getFile().getName().getBaseName());
                // Handle file change
            }
        };

        DefaultFileMonitor fileMonitor = new DefaultFileMonitor(listener);
        fileMonitor.addFile(remoteDir);
        fileMonitor.start();
        log.info("File monitoring started successfully.");

    }

}
