package ru.croc.ctp.just.bot.telegram;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Класс со вспомогательными методами.
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

    /**
     * Метод проверяет, начинается ли текст сообщения с переданной строки.
     * @param update сообщение
     * @param str строка с которой сравниваем
     * @return true - сообщение начинается с переданной строки, иначе - false
     */
    public static boolean updateTextStartsWith(Update update, String str) {
        return update.getMessage().hasText() && update.getMessage().getText().startsWith(str);
    }

    /**
     * Метод проверяет идентичен ли текст сообщения с переданной строки.
     * @param update сообщение
     * @param str строка с которой сравниваем
     * @return true - сообщение равно переданной строке, иначе - false
     */
    public static boolean updateTextEquals(Update update, String str) {
        return update.getMessage().hasText() && update.getMessage().getText().equals(str);
    }

    /**
     * Метод для получения ip из команды.
     * @param text текст команды.
     * @return ip или null, если распознать ip не удалось
     */
    public static String getIpFromCommand(String text) {
        String[] splitedText = text.split(" ");
        if (splitedText.length != 2 || !InetAddressValidator.getInstance().isValid(splitedText[1])) {
            return null;
        }
        return splitedText[1];
    }

}
