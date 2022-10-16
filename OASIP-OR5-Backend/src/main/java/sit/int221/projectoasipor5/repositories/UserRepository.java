package sit.int221.projectoasipor5.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.projectoasipor5.entities.User;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);

    User findByName(String name);

    boolean existsByEmail(String email);
}
