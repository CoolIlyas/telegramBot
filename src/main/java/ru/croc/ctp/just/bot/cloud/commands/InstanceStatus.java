package ru.croc.ctp.just.bot.cloud.commands;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.StaxResponseHandler;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.transform.DescribeInstancesResultStaxUnmarshaller;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.cloud.service.CloudService;
import ru.croc.ctp.just.bot.cloud.service.InstanceIpToIdService;
import ru.croc.ctp.just.bot.telegram.ChatEntity;
import ru.croc.ctp.just.bot.telegram.Command;

import java.util.List;
import java.util.Map;

import static ru.croc.ctp.just.bot.telegram.BotUtil.sendMessage;

/**
 * Команда для получения статуса экземпляра.
 */
@Component
@RequiredArgsConstructor
public class InstanceStatus implements Command {
    /**
     * Сервис для получения id экземпляра.
     */
    private final InstanceIpToIdService instanceIpToIdService;
    /**
     * Сервис для отправки запросов в облако.
     */
    private final CloudService cloudService;

    @Override
    public boolean isCalled(Update update) {
        return update.getMessage().hasText() && update.getMessage().getText().startsWith("/instanceStatus");
    }

    @Override
    public List<Object> handle(Update update, ChatEntity chat) {
        String text = update.getMessage().getText();
        String[] splitedText = text.split(" ");
        if (splitedText.length != 2 || !InetAddressValidator.getInstance().isValid(splitedText[1])) {
            return sendMessage("Не удалось распознать ip", chat);
        }
        String id = instanceIpToIdService.getId(splitedText[1]);
        if (id == null) {
            return sendMessage("Экземпляр с данным ip не найден", chat);
        }
        try {
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
        } catch (Exception e) {
            return sendMessage("Произошла ошибка при обращении на сервер", chat);
        }
    }

    @Override
    public String description() {
        return "/instanceStatus <ip> - Узнать статус экземпляра";
    }
}
