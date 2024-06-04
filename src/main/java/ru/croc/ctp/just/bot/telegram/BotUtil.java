package ru.croc.ctp.just.bot.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

/**
 * Класс со вспомогательными методамию
 */
public class BotUtil {
    /**
     * Метод для формирования сообщения.
     * @param message текст
     * @param chat чат в который нужно отправить сообщение
     * @return лист с сообщением
     */
    public static List<Object> sendMessage(String message, ChatEntity chat) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat.getId());
        sendMessage.setText(message);
        return List.of(sendMessage);
    }
}
