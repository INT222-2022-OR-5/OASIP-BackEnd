package sit.int221.projectoasipor5.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.projectoasipor5.entities.CategoryOwner;
import sit.int221.projectoasipor5.entities.CategoryOwnerId;
import sit.int221.projectoasipor5.entities.User;

import java.util.List;

public interface CategoryOwnerRepository extends JpaRepository<CategoryOwner, CategoryOwnerId> {
    List<CategoryOwner> findByUserId(User id);
}