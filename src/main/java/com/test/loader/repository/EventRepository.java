package com.test.loader.repository;

import com.test.loader.model.EventEntry;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<EventEntry, String> {

}
