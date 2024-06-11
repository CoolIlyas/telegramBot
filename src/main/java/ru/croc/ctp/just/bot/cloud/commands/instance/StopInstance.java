package ru.croc.ctp.just.bot.cloud.commands.instance;

import com.amazonaws.http.HttpMethodName;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.cloud.service.CloudService;
import ru.croc.ctp.just.bot.cloud.service.InstanceIpToIdService;
import ru.croc.ctp.just.bot.telegram.ChatEntity;

import java.util.List;
import java.util.Map;

import static ru.croc.ctp.just.bot.telegram.BotUtil.sendMessage;

/**
 * Команда для остановки работы экземпляра.
 */
@Component
public class StopInstance extends BaseInstanceCommand {

    /**
     * Конструктор.
     * @param instanceIpToIdService Сервис для получения id экземпляра
     * @param cloudService          Сервис для отправки запросов в облако
     */
    public StopInstance(InstanceIpToIdService instanceIpToIdService, CloudService cloudService) {
        super(instanceIpToIdService, cloudService);
    }

    @Override
    public boolean isCalled(Update update) {
        return update.getMessage().hasText() && update.getMessage().getText().startsWith("/stopInstance");
    }

    @Override
    protected List<Object> makeRequest(String id, ChatEntity chat) {
        cloudService.sendRequest("StopInstances", HttpMethodName.GET, Map.of("InstanceId.1", id), null);
        return sendMessage("Готово", chat);
    }



    @Override
    public String description() {
        return "/stopInstance <ip> - Остановить работу экземпляра";
    }
}
