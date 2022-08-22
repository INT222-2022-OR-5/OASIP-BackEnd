package sit.int221.projectoasipor5.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.projectoasipor5.entities.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Integer> {
    List<Event> findAllByOrderByEventStartTimeDesc();
}
