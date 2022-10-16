package sit.int221.projectoasipor5.dto.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.projectoasipor5.dto.EventCategory.EventCategoryDTO;

import javax.validation.constraints.*;
import java.time.Instant;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDTO {
    @NotBlank(message = "BookingEmail must not be blank")
    @Email(message = "BookingEmail must be a well-formed email address")
    @Size(min = 1 , max = 100 , message = "BookingEmail must between 1 - 100")
    private String bookingEmail;

    @NotNull(message = "StartTime must not be null")
    @FutureOrPresent(message = "StartTime must be a future or present date")
    private Instant eventStartTime;

    @Size(max = 500 , message = "Notes must between 0 - 500")
    private String eventNotes;

    private Integer eventDuration;

    private EventCategoryDTO eventCategory;
}
