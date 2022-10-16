package sit.int221.projectoasipor5.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@ToString
@AllArgsConstructor
@Getter  @Setter
@Entity @Table(name = "EventCategory")
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventCategoryId", nullable = false)
    private Integer id;

    @NotBlank(message = "CategoryName must not be blank")
    @NotNull(message = "CategoryName must not be null")
    @NotEmpty(message = "CategoryName must not be empty")
    @Size(min = 1 , max = 100 , message = "CategoryName must between 1 - 100")
    @Column(name = "eventCategoryName", nullable = false, length = 100)
    private String eventCategoryName;

    @Size(max = 500 , message = "CategoryDescription must between 0 - 500")
    @Column(name = "eventCategoryDescription", length = 500)
    private String eventCategoryDescription;

    @Max(value = 480, message = "Duration have more than 480 minute")
    @Min(value = 1, message = "Duration have less 1 minute")
    @NotNull(message = "Duration must not be null")
    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

    @JsonIgnore
    @OneToMany(mappedBy = "eventCategory")
    private Set<Event> events = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "eventCategories")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    public EventCategory() {
    }
}
