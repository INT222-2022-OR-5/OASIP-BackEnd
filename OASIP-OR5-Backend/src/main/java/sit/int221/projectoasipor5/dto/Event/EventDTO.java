package sit.int221.projectoasipor5.dto.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.projectoasipor5.dto.EventCategory.EventCategoryDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.*;
import java.time.Instant;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "BookingName must not be blank")
    @Size(min = 1 , max = 100 , message = "BookingName must between 1 - 100")
    private String bookingName;

    @NotBlank(message = "BookingEmail must not be blank")
    @Email(message = "BookingEmail must be a well-formed email address")
    @Size(min = 1 , max = 100 , message = "BookingEmail must between 1 - 100")
    private String bookingEmail;

    @NotNull(message = "StartTime must not be null")
    @FutureOrPresent(message = "StartTime must be a future or present date")
    private Instant eventStartTime;

    @NotNull(message = "Duration must not be null")
    private Integer eventDuration;

    @Size(max = 500 , message = "Notes must between 0 - 500")
    private String eventNotes;

    @NotNull(message = "EventCategory must not be null")
    private EventCategoryDTO eventCategory;

    private Integer userId;
}
