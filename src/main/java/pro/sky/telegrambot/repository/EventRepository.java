package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

//    @Query(value = "SELECT * FROM event WHERE event_date = localDateTime", nativeQuery = true)
    public List<Event> findByEventDate(LocalDateTime eventDate);

}
