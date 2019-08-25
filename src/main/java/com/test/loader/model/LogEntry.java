package com.test.loader.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class LogEntry {

    private String id;
    private LogState state;
    private Long timestamp;
    private String type;
    private String host;
}
