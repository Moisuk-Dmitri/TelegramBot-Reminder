package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Event;
import pro.sky.telegrambot.repository.EventRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final EventRepository eventRepository;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    public TelegramBotUpdatesListener(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            if (update.message().text().equals("/start")) {
                SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                        "Введите информацию для напоминания в формате:\n" +
                                "дд.мм.гггг чч:мм {текст напоминания}");
                telegramBot.execute(sendMessage);
            } else if (Pattern.matches("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)", update.message().text())) {
                String eventDateTimeString = update.message().text().substring(0, 16);
                LocalDateTime eventDateTime = LocalDateTime.parse(eventDateTimeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                String eventText = update.message().text().substring(17);

                Event event = new Event();
                event.setEventDate(eventDateTime);
                event.setEventText(eventText);
                event.setChatId(update.message().chat().id());

                eventRepository.save(event);

                SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                        "Напоминание сохранено");
                telegramBot.execute(sendMessage);
            } else {
                SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                        "Неправильный формат напоминания");
                telegramBot.execute(sendMessage);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "${telegram.bot.scheduledCronExpression}")
    public void remind() {
        LocalDateTime dateTime_curr = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<Event> eventList = eventRepository.findByEventDate(dateTime_curr);

        if (!eventList.isEmpty()) {
            eventList.forEach(event -> {
                logger.info("Processing event: {}", event);

                SendMessage sendMessage = new SendMessage(event.getChatId(),
                        event.getEventText());
                telegramBot.execute(sendMessage);

                eventRepository.delete(event);
            });
        }
    }

}
