package com.test.loader.services

import com.test.loader.model.EventEntry
import com.test.loader.model.LogEntry
import com.test.loader.model.LogState
import com.test.loader.repository.EventRepository
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue

class EntryWriterServiceTest extends Specification {

    def fileReaderMock = Mock(FileReaderService)
    def eventRepoMock = Mock(EventRepository)
    def queue = new ArrayBlockingQueue(10)
    def threshold = 4
    def instance = new EntryWriterService(eventRepoMock, queue, fileReaderMock, threshold)

    def logEntry1 = new LogEntry().with {id = "ddd"; state = LogState.STARTED; timestamp = 997L; it}
    def logEntry2 = new LogEntry().with {id = "ddd"; state = LogState.FINISHED; timestamp = 1001L; host= "avsd"; type="test";it}
    def logEntry3 = new LogEntry().with {id = "ddd2"; state = LogState.FINISHED; timestamp = 1001L; host= "avsd"; type="test";it}


    def "should correctly map 2 log entries"() {
        given:
        queue.addAll([logEntry1, logEntry2])
        when:
        instance.processEvent()
        then:
        fileReaderMock.isProcessed() >>> [false, false, true]
        1 * eventRepoMock.save({it -> it.getId() == "ddd" && it.getDuration() == 4L && it.getHost() == "avsd" && it.getType() == "test" && !it.getIsAlert() })
    }

    def "should correctly map 2 log entries and add alert"() {
        given:
        logEntry2.setTimestamp(1002L)
        queue.addAll([logEntry1, logEntry2])
        when:
        instance.processEvent()
        then:
        fileReaderMock.isProcessed() >>> [false, false, true]
        1 * eventRepoMock.save({it -> it.getId() == "ddd" && it.getDuration() == 5L && it.getHost() == "avsd" && it.getType() == "test" && it.getIsAlert() })
    }

    def "should correctly map 2 log entries with different timestamps"() {
        given:
        logEntry2.setTimestamp(992L)
        queue.addAll([logEntry1, logEntry2])
        when:
        instance.processEvent()
        then:
        fileReaderMock.isProcessed() >>> [false, false, true]
        1 * eventRepoMock.save({it -> it.getId() == "ddd" && it.getDuration() == 5L && it.getHost() == "avsd" && it.getType() == "test" && it.getIsAlert() })
    }

    def "should skip non matched logs"() {
        given:
        queue.addAll([logEntry1, logEntry2, logEntry3])
        when:
        instance.processEvent()
        then:
        fileReaderMock.isProcessed() >>> [false, false, false, true]
        1 * eventRepoMock.save({it -> it.getId() == "ddd" && it.getDuration() == 4L && it.getHost() == "avsd" && it.getType() == "test" && !it.getIsAlert() })
    }
}
