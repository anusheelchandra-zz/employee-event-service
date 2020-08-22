package employeeservice.domain.message;

import employeeservice.domain.Action;
import java.time.LocalDateTime;
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

  @NonNull private String employeeUuid;

  @NonNull private Action action;

  @NonNull private LocalDateTime timestamp;
}
