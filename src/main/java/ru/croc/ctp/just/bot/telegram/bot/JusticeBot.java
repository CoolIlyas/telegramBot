package ru.croc.ctp.just.bot.telegram.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Бот.
 */
@Component
public class JusticeBot extends TelegramLongPollingBot {

    private final MessageHandler messageHandler;

    @Value("${telegram.bot.username}")
    private String botUsername;

    /**
     * Геттер юзернейма бота.
     * @return юзернейм
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Конструктор.
     * @param botToken токен бота
     * @param messageHandler обработчик сообщений
     */
    public JusticeBot(@Value("${telegram.bot.token}") String botToken, MessageHandler messageHandler) {
        super(botToken);
        this.messageHandler = messageHandler;
    }

    /**
     * Все сообщения бота приходят сюда.
     * @param update Update received
     */
    @Override
    public void onUpdateReceived(Update update) {
        List<Object> answers = messageHandler.handle(update);
        answers.forEach(answer -> {
            try {
                if (answer instanceof SendMessage) {
                    execute((SendMessage) answer);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }
}
