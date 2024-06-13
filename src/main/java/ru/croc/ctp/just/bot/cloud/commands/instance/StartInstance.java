package ru.croc.ctp.just.bot.cloud.commands.instance;

import com.amazonaws.http.HttpMethodName;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.cloud.service.CloudService;
import ru.croc.ctp.just.bot.cloud.service.InstanceIpToIdService;
import ru.croc.ctp.just.bot.telegram.ChatEntity;

import java.util.List;
import java.util.Map;

import static ru.croc.ctp.just.bot.cloud.service.CloudService.INSTANCE_ID;
import static ru.croc.ctp.just.bot.cloud.service.CloudService.START_INSTANCES;
import static ru.croc.ctp.just.bot.telegram.BotUtil.sendMessage;
import static ru.croc.ctp.just.bot.telegram.BotUtil.updateTextStartsWith;

/**
 * Команда для запуска экземпляра.
 */
@Component
public class StartInstance extends BaseInstanceCommand {

    /**
     * Конструктор.
     * @param instanceIpToIdService Сервис для получения id экземпляра
     * @param cloudService          Сервис для отправки запросов в облако
     */
    public StartInstance(InstanceIpToIdService instanceIpToIdService, CloudService cloudService) {
        super(instanceIpToIdService, cloudService);
    }

    @Override
    public boolean isCalled(Update update) {
        return updateTextStartsWith(update, "/startInstance");
    }

    @Override
    protected List<Object> makeRequest(String id, ChatEntity chat) {
        cloudService.sendRequest(START_INSTANCES, HttpMethodName.GET, Map.of(INSTANCE_ID + "1", id), null);
        return sendMessage("Готово", chat);
    }

    @Override
    public String description() {
        return "/startInstance <ip> - Запустить экземпляр";
    }
}
