package ru.croc.ctp.just.bot.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Базовый интерфейс всех команд.
 */
public interface Command {
    /**
     * Проверяет вызывается ли эта команда.
     * @param update сообщение
     * @return true - если вызывается, false - если нет
     */
    boolean isCalled(Update update);

    /**
     * Обработка команды.
     * @param update сообщение
     * @param chat чат сообщения
     * @return ответ пользователю.
     */
    List<Object> handle(Update update, ChatEntity chat);

    /**
     * Описание команды для /help.
     * @return строка с описанием
     */
    String description();
}
