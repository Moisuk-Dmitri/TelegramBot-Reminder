package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String eventText;
    
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    public Event() {
    }

    public Event(Long chatId, String eventText, LocalDateTime eventDate) {
        this.chatId = chatId;
        this.eventText = eventText;
        this.eventDate = eventDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getEventText() {
        return eventText;
    }

    public void setEventText(String eventText) {
        this.eventText = eventText;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id && Objects.equals(chatId, event.chatId) && Objects.equals(eventText, event.eventText) && Objects.equals(eventDate, event.eventDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, eventText, eventDate);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", chatId='" + chatId + '\'' +
                ", eventText='" + eventText + '\'' +
                ", eventDate=" + eventDate +
                '}';
    }
}
