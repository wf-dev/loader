package com.test.loader;

import com.test.loader.services.EntryWriterService;
import com.test.loader.services.FileReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;


@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    @Autowired
    private FileReaderService fileReaderService;

    @Autowired
    private EntryWriterService writerService;

    public static void main(String[] args) throws Exception {
        log.info("Application started");
        SpringApplication.run(Application.class, args);
        log.info("Application finished");

    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Will read file from arguments list");
        log.info("Arguments: {} ", String.join(", ", args));
        if (args.length < 1) {
            throw new IllegalArgumentException("Add file name");
        }
        File file = new File(args[0]);
        if (file.exists() && file.canRead()) {
            fileReaderService.readFileAndAddToQueue(file);
            writerService.processEvent();
            fileReaderService.shutdown();
        } else {
            throw new IllegalArgumentException("Unable to open or read file: " + file.getAbsolutePath());
        }
    }
}
