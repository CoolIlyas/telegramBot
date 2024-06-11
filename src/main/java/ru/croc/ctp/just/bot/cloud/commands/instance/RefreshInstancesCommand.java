package ru.croc.ctp.just.bot.cloud.commands.instance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.cloud.service.InstanceIpToIdService;
import ru.croc.ctp.just.bot.telegram.ChatEntity;
import ru.croc.ctp.just.bot.telegram.Command;

import java.util.List;

import static ru.croc.ctp.just.bot.telegram.BotUtil.sendMessage;

/**
 * Команда для обновления данных о доступных экземплярах.
 */
@Component
@RequiredArgsConstructor
public class RefreshInstancesCommand implements Command {
    /**
     * Сервис для получения id экземпляра.
     */
    private final InstanceIpToIdService instanceIpToIdService;

    @Override
    public boolean isCalled(Update update) {
        return update.getMessage().hasText() && update.getMessage().getText().equals("/refreshInstances");
    }

    @Override
    public List<Object> handle(Update update, ChatEntity chat) {
        try {
            instanceIpToIdService.refreshMap();
            return sendMessage("Готово!", chat);
        } catch (Exception e) {
            return sendMessage("Произошла ошибка при обращении на сервер", chat);
        }
    }

    @Override
    public String description() {
        //скрытая команда
        return null;
    }
}
