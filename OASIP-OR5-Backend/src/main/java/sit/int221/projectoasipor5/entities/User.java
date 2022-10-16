package sit.int221.projectoasipor5.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 14)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "createdOn", nullable = false, updatable = false, insertable = false)
    private Instant createdOn;

    @Column(name = "updatedOn", nullable = false, updatable = false, insertable = false)
    private Instant updatedOn;

    @OneToMany(mappedBy = "user")
    private Set<Event> events = new LinkedHashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "CategoryOwner", joinColumns = { @JoinColumn(name = "userId") }, inverseJoinColumns = { @JoinColumn(name = "eventCategoryId") })
    private Set<EventCategory> eventCategories = new HashSet<>();

    public void addEventCategory(EventCategory eventCategory) {
        this.eventCategories.add(eventCategory);
        eventCategory.getUsers().add(this);
    }
}
