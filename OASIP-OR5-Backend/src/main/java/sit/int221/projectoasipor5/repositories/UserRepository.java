package sit.int221.projectoasipor5.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.projectoasipor5.entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    @Query(value = "select * from User s where s.name like ?1",nativeQuery = true)
    List<User> findNameUnique(String name);

    @Query(value = "select * from User s where s.email like ?1",nativeQuery = true)
    List<User> findEmailUnique(String email);

    @Query(value = "select * from User u where u.email like ?1",nativeQuery = true)
    User matchPass(String email);
}
