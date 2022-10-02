package sit.int221.projectoasipor5.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.sql.Timestamp;

import javax.persistence.*;

@Getter @Setter
@Entity @Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Lob
    @Column(name = "role", nullable = false)
    private String role;

    @CreationTimestamp
    @Column(name = "createdOn", insertable = false)
    private Timestamp createdOn;

    @UpdateTimestamp
    @Column(name = "updatedOn", updatable = false)
    private Timestamp updatedOn;
}
