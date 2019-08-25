package com.test.loader.services;

import com.test.loader.model.EventEntry;
import com.test.loader.model.LogEntry;
import com.test.loader.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@AllArgsConstructor
@Slf4j
public class EntryWriterService {

    private final EventRepository eventRepository;
    private final ArrayBlockingQueue<LogEntry> globalQueue;
    private final FileReaderService fileReaderService;
    private final int threshold;
    private final Map<String, LogEntry> logEntryMap= new HashMap<>();

    public void processEvent() {
        int eventCounter = 0;
        while (!globalQueue.isEmpty() || !fileReaderService.isProcessed()) {
            LogEntry logEntry = null;
            try {
                logEntry = globalQueue.poll(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (logEntry == null || Strings.isEmpty(logEntry.getId())) {
               log.warn("log entry or id empty, skipping");
               continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("polling: {}", logEntry.getId());
            }
            if (logEntryMap.containsKey(logEntry.getId())) {
                LogEntry previuousEntry = logEntryMap.get(logEntry.getId());
                calculateAndStore(logEntry, previuousEntry);
                eventCounter++;
                logEntryMap.remove(logEntry.getId());
            } else {
                logEntryMap.put(logEntry.getId(), logEntry);
            }
        }
        log.info("All events ({}) processed and saved", eventCounter);

        if (!logEntryMap.isEmpty()) {
            log.info("Events not matched: {}", logEntryMap.keySet().stream().collect(Collectors.joining(", ")));
        }
    }

    private void calculateAndStore(LogEntry firstEntry, LogEntry secondEntry) {
        Long time = Math.abs(secondEntry.getTimestamp() - firstEntry.getTimestamp());
        EventEntry eventEntry = new EventEntry(firstEntry.getId(),
                time,
                Stream.of(firstEntry, secondEntry).filter(log -> Strings.isNotEmpty(log.getType())).findAny().map(LogEntry::getType).orElse(null),
                Stream.of(firstEntry, secondEntry).filter(log -> Strings.isNotEmpty(log.getHost())).findAny().map(LogEntry::getHost).orElse(null),
                time > threshold);
        //TODO: multiple insert to increase performance
        eventRepository.save(eventEntry);
    }

}
