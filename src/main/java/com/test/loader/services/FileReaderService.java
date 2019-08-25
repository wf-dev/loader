package com.test.loader.services;

import com.google.gson.Gson;
import com.test.loader.Application;
import com.test.loader.model.LogEntry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public class FileReaderService {

    private final Gson gson;
    private final ArrayBlockingQueue<LogEntry> globalQueue;

    private final Executor executor = Executors.newFixedThreadPool(1);
    private boolean isProcessed = false;

    public FileReaderService(Gson gson, ArrayBlockingQueue<LogEntry> globalQueue) {
        this.gson = gson;
        this.globalQueue = globalQueue;
    }

    public void readFileAndAddToQueue(File file) {
        Runnable runnable = () -> {
            int logsCounter = 0;
            try {
                BufferedReader reader = Files.newBufferedReader(file.toPath());
                String line = reader.readLine();
                while (line != null) {
                    LogEntry logEntry = gson.fromJson(line, LogEntry.class);
                    if (log.isDebugEnabled()) {
                        log.debug("processing log id: {} with state: {}", logEntry.getId(), logEntry.getState());
                    }
                    if (logEntry.getId() != null && logEntry.getTimestamp() != null) {
                        globalQueue.put(logEntry);
                        logsCounter++;
                    } else {
                        log.warn("Log id or timestamp null, skipping entry");
                    }
                    line = reader.readLine();
                }
            } catch (IOException | InterruptedException e) {
                log.warn("Exception during processing", e);
                e.printStackTrace();
            } finally {
                log.info("Processed {} log entries", logsCounter);
                isProcessed = true;
            }
        };
        executor.execute(runnable);
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void shutdown() {
        //executor.
    }
}
