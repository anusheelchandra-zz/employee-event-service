package com.eventservice.mapper;

import com.eventservice.domain.Action;
import com.eventservice.util.TestUtil;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EventDTOMapperTest {

  @Test
  public void shouldMapAndReturnNonEmptyEventDTOList() {
    String EMP_UUID = "d881e278-9148-4ea2-b4fc-1735978ac4a";
    var eventDTOS = EventDTOMapper.toEventDTOs(TestUtil.createEvents(EMP_UUID));
    Assertions.assertThat(eventDTOS).isNotEmpty();
    Assertions.assertThat(eventDTOS).hasSize(2);
    TestUtil.commonAssert(eventDTOS.get(0), EMP_UUID, Action.CREATE, "2020-08-18T10:24:58.62956");
    TestUtil.commonAssert(eventDTOS.get(1), EMP_UUID, Action.UPDATE, "2020-08-18T11:24:58.62956");
  }

  @Test
  public void shouldMapAndReturnEmptyEventDTOList() {
    var eventDTOS = EventDTOMapper.toEventDTOs(Collections.emptyList());
    Assertions.assertThat(eventDTOS).isEmpty();
  }
}
