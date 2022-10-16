package sit.int221.projectoasipor5.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Instant;
import java.time.ZonedDateTime;

//@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @FutureOrPresent(message = "StartTime must be a future or present date")
    @JsonFormat(timezone="Asia/Bangkok")
    @Column(name = "eventStartTime", nullable = false)
    private Instant eventStartTime;

    @Column(name = "eventDuration" , nullable = false)
    private Integer eventDuration;

    @Lob
    @Size(max = 500 , message = "Notes must between 0 - 500")
    @Column(name = "eventNotes", length = 500)
    private String eventNotes;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eventCategoryId", nullable = false)
    private EventCategory eventCategory;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public Event() {
    }
}
