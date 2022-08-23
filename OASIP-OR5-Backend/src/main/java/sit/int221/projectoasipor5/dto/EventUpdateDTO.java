package sit.int221.projectoasipor5.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDTO {
    private Integer id;

    @NotNull(message = "StartTime must not be null")
    @Future(message = "StartTime must be a future date")
    private ZonedDateTime eventStartTime;

    @Size(max = 500 , message = "Notes must between 0 - 500")
    private String eventNotes;
}
