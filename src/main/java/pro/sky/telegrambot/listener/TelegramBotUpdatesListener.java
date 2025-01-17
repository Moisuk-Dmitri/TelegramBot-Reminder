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
    private final TelegramBot telegramBot;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final String reminderPattern = "(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)";

    public TelegramBotUpdatesListener(EventRepository eventRepository, TelegramBot telegramBot) {
        this.eventRepository = eventRepository;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            String messageText = update.message().text();
            Long chatId = update.message().chat().id();

            if (messageText.isEmpty()) {
                logger.error("Empty message");
            } else if (messageText.equals("/start")) {
                sendMessage(chatId,
                        "Введите информацию для напоминания в формате:\n" +
                        "дд.мм.гггг чч:мм {текст напоминания}");
            } else if (Pattern.matches(reminderPattern, messageText)) {
                String eventDateTimeString = messageText.substring(0, 16);
                LocalDateTime eventDateTime = LocalDateTime.parse(eventDateTimeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                String eventText = messageText.substring(17);

                Event event = new Event();
                event.setEventDate(eventDateTime);
                event.setEventText(eventText);
                event.setChatId(chatId);

                eventRepository.save(event);

                sendMessage(chatId,
                        "Напоминание сохранено");
            } else {
                sendMessage(chatId,
                        "Неправильный формат напоминания");
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId,
                text);
        telegramBot.execute(sendMessage);
    }
}
