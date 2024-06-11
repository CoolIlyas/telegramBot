package ru.croc.ctp.just.bot.cloud.commands.instance;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.StaxResponseHandler;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.transform.DescribeInstancesResultStaxUnmarshaller;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.cloud.service.CloudService;
import ru.croc.ctp.just.bot.cloud.service.InstanceIpToIdService;
import ru.croc.ctp.just.bot.telegram.ChatEntity;

import java.util.List;
import java.util.Map;

import static ru.croc.ctp.just.bot.telegram.BotUtil.sendMessage;

/**
 * Команда для получения статуса экземпляра.
 */
@Component
public class InstanceStatus extends BaseInstanceCommand {

    /**
     * Конструктор.
     * @param instanceIpToIdService Сервис для получения id экземпляра
     * @param cloudService          Сервис для отправки запросов в облако
     */
    public InstanceStatus(InstanceIpToIdService instanceIpToIdService, CloudService cloudService) {
        super(instanceIpToIdService, cloudService);
    }

    @Override
    public boolean isCalled(Update update) {
        return update.getMessage().hasText() && update.getMessage().getText().startsWith("/instanceStatus");
    }

    @Override
    protected List<Object> makeRequest(String id, ChatEntity chat) {
        DescribeInstancesResult result = cloudService.sendRequest("DescribeInstances",
                HttpMethodName.GET, Map.of("InstanceId.1", id),
                new StaxResponseHandler<>(new DescribeInstancesResultStaxUnmarshaller())).getResult();
        Integer status = result.getReservations().get(0).getInstances().get(0).getState().getCode();
        String answer = switch (status) {
            case 0 -> "Запускается";
            case 16 -> "В работе";
            case 32 -> "Выключение";
            case 48 -> "Выключено";
            case 64 -> "В процессе приостановки работы";
            case 80 -> "Работа приостановлена";
            default -> "Не удалось распознать статус";
        };
        return sendMessage("Статус экземпляра: " + answer, chat);
    }

    @Override
    public String description() {
        return "/instanceStatus <ip> - Узнать статус экземпляра";
    }
}
