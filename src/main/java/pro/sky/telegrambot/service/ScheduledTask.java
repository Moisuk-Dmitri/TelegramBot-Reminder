package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.Event;
import pro.sky.telegrambot.repository.EventRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ScheduledTask {

    private final EventRepository eventRepository;
    private final TelegramBotUpdatesListener telegramBot;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    public ScheduledTask(EventRepository eventRepository, TelegramBotUpdatesListener telegramBot) {
        this.eventRepository = eventRepository;
        this.telegramBot = telegramBot;
    }

    @Scheduled(cron = "${telegram.bot.scheduledCronExpression}")
    public void remind() {
        LocalDateTime dateTime_curr = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<Event> eventList = eventRepository.findByEventDate(dateTime_curr);

        if (!eventList.isEmpty()) {
            eventList.forEach(event -> {
                logger.info("Processing event: {}", event);

                telegramBot.sendMessage(event.getChatId(), event.getEventText());

                eventRepository.delete(event);
            });
        }
    }
}
