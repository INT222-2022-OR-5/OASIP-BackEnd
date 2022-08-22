package sit.int221.projectoasipor5.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.ZonedDateTime;
import java.time.format.*;
import java.util.Date;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Integer id;

    @NotBlank(message = "BookingName must not be blank")
    @Size(min = 1 , max = 100 , message = "BookingName must between 1 - 100")
    private String bookingName;

    @NotBlank(message = "BookingEmail must not be blank")
    @Email(message = "BookingEmail must be a well-formed email address")
    @Size(min = 1 , max = 100 , message = "BookingEmail must between 1 - 100")
    private String bookingEmail;

    @NotNull(message = "StartTime must not be null")
    @Future(message = "StartTime must be a future date")
    private ZonedDateTime eventStartTime;

    private Integer eventDuration;

    @Size(max = 500 , message = "Notes must between 0 - 500")
    private String eventNotes;

    private EventCategoryDTO eventCategory;
}
