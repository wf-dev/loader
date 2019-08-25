package com.test.loader.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class EventEntry {

    @Id
    private String id;

    private Long duration;

    private String type;

    private String host;

    private Boolean isAlert;

}
