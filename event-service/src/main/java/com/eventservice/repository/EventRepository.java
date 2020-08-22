package com.eventservice.repository;

import com.eventservice.entity.Event;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

  List<Event> findAllByEmployeeUuid(String employeeUuid, Sort sort);
}
