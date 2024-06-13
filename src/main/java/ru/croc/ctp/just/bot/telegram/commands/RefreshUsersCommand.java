package ru.croc.ctp.just.bot.telegram.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.security.SecurityService;
import ru.croc.ctp.just.bot.telegram.ChatEntity;
import ru.croc.ctp.just.bot.telegram.Command;

import java.util.List;

import static ru.croc.ctp.just.bot.telegram.BotUtil.sendMessage;
import static ru.croc.ctp.just.bot.telegram.BotUtil.updateTextEquals;

/**
 * Команда обновляет список допустимых пользователей из файла.
 */
@Component
@RequiredArgsConstructor
public class RefreshUsersCommand implements Command {
    /**
     * Сервис для авторизации пользователей.
     */
    private final SecurityService securityService;

    @Override
    public boolean isCalled(Update update) {
        return updateTextEquals(update, "/refreshUsers");
    }

    @Override
    public List<Object> handle(Update update, ChatEntity chat) {
        try {
            securityService.refreshUserSet();
            return sendMessage("Готово!", chat);
        } catch (Exception e) {
            return sendMessage("Произошла ошибка. " + e.getMessage(), chat);
        }
    }

    @Override
    public String description() {
        // Скрытая команда.
        return null;
    }
}
