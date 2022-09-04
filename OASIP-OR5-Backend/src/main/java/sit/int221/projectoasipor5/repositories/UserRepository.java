package sit.int221.projectoasipor5.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.projectoasipor5.entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
    @Query(value = "select * from user s where s.name = :name",nativeQuery = true)
    List<User> findNameUnique(String name);

    @Query(value = "select * from user s where s.email = :email",nativeQuery = true)
    List<User> findEmailUnique(String email);

    @Query(value = "select * from user u where u.email like ?1",nativeQuery = true)
    public User matchPass(String email);
}
