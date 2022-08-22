package sit.int221.projectoasipor5.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity @Table(name = "Event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventId", nullable = false)
    private Integer id;

    @NotNull(message = "BookingName must not be null")
    @NotEmpty(message = "BookingName must not be empty")
    @Size(min = 1 , max = 100 , message = "BookingName must between 1 - 100")
    @Column(name = "bookingName", nullable = false, length = 100)
    private String bookingName;

    @NotNull(message = "BookingEmail must not be null")
    @NotEmpty(message = "BookingEmail must not be empty")
    @Email(message = "BookingEmail must be a well-formed email address")
    @Size(min = 1 , max = 100 , message = "BookingEmail must between 1 - 100")
    @Column(name = "bookingEmail", length = 100)
    private String bookingEmail;

    @NotNull(message = "StartTime must not be null")
    @Future(message = "StartTime must be a future date")
    @Column(name = "eventStartTime", nullable = false)
    private ZonedDateTime eventStartTime;

    @Column(name = "eventDuration" , nullable = false)
    private Integer eventDuration;

    @Size(max = 500 , message = "Notes must between 0 - 500")
    @Column(name = "eventNotes", length = 500)
    private String eventNotes;

    @ManyToOne
    @JoinColumn(name = "eventCategoryId", nullable = false)
    private EventCategory eventCategory;
}
