package com.test.loader.config;

import com.google.gson.Gson;
import com.test.loader.model.LogEntry;
import com.test.loader.repository.EventRepository;
import com.test.loader.services.EntryWriterService;
import com.test.loader.services.FileReaderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class AppConfig {

    private ArrayBlockingQueue<LogEntry> globalQueue = new ArrayBlockingQueue<>(1000);
    private Gson gson = new Gson();
    private int threshold = 4; //TODO: should be in config file

    @Bean
    public FileReaderService fileReaderService() {
        return new FileReaderService(gson, globalQueue);
    }

    @Bean
    public EntryWriterService entryWriterService(EventRepository eventRepository, FileReaderService fileReaderService) {
        return new EntryWriterService(eventRepository, globalQueue, fileReaderService, threshold);
    }
}
