package ru.croc.ctp.just.bot.cloud.commands.instance;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.cloud.service.CloudService;
import ru.croc.ctp.just.bot.cloud.service.InstanceIpToIdService;
import ru.croc.ctp.just.bot.telegram.ChatEntity;
import ru.croc.ctp.just.bot.telegram.Command;

import java.util.List;

import static ru.croc.ctp.just.bot.telegram.BotUtil.getIpFromCommand;
import static ru.croc.ctp.just.bot.telegram.BotUtil.sendMessage;

/**
 * Базовый класс для команд работы с экземплярами.
 */
@RequiredArgsConstructor
public abstract class BaseInstanceCommand implements Command {
    /**
     * Сервис для получения id экземпляра.
     */
    protected final InstanceIpToIdService instanceIpToIdService;
    /**
     * Сервис для отправки запросов в облако.
     */
    protected final CloudService cloudService;

    @Override
    public List<Object> handle(Update update, ChatEntity chat) {
        String ip = getIpFromCommand(update.getMessage().getText());
        if (ip == null) {
            return sendMessage("Не удалось распознать ip", chat);
        }
        String id = instanceIpToIdService.getId(ip);
        if (id == null) {
            return sendMessage("Экземпляр с данным ip не найден", chat);
        }
        try {
            return makeRequest(id, chat);
        } catch (Exception e) {
            return sendMessage("Произошла ошибка при обращении на сервер", chat);
        }
    }

    /**
     * Метод делает запрос в облако для данного экземпляра.
     * @param id id экземпляра
     * @param chat чат с пользователем
     * @return лист с сообщениями для пользователя
     */
    protected abstract List<Object> makeRequest(String id, ChatEntity chat);
}
