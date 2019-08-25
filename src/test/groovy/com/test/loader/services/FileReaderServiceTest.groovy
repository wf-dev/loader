package com.test.loader.services

import com.google.gson.Gson
import com.test.loader.model.LogEntry
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue

class FileReaderServiceTest extends Specification {

    def gson = new Gson()
    def queue = new ArrayBlockingQueue<LogEntry>(100)

    def instance = new FileReaderService(gson, queue)

    def file = new File("src/test/resources/correntvalues.txt")
    def file2 = new File("src/test/resources/brokenvalues.txt")
    def file3 = new File("src/test/resources/maxvalues.txt")

    def "should read file"() {
        when:
        instance.readFileAndAddToQueue(file)
        then:
        queue.size() == 6
    }

    def "should read file and skip broken records"() {
        when:
        instance.readFileAndAddToQueue(file2)
        then:
        queue.size() == 6
    }

    def "should read file and "() {
        expect:
        file3.write("")
        for(int counter=0;counter<100000;counter++) {
            file3.append("{\"id\":\"scsmbstgrc${counter}\", \"state\":\"STARTED\", \"timestamp\":1491377495210}\n" +
                        "{\"id\":\"scsmbstgrc${counter}\", \"state\":\"FINISHED\", \"timestamp\":1491377495216}\n")
        }
        true
    }
}
