package com.eventservice.domain.message;

import com.eventservice.domain.Action;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage {

  @NotNull @NotEmpty private String employeeUuid;

  @NonNull private Action action;

  @NotNull private LocalDateTime timestamp;
}
