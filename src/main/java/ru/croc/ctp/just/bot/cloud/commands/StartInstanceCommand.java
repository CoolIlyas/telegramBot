package ru.croc.ctp.just.bot.cloud.commands;

import com.amazonaws.http.HttpMethodName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.cloud.service.CloudService;
import ru.croc.ctp.just.bot.telegram.Command;
import ru.croc.ctp.just.bot.telegram.ChatEntity;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StartInstanceCommand implements Command {

    private final CloudService cloudService;

    @Override
    public boolean isCalled(Update update) {
        return update.getMessage().hasText() && update.getMessage().getText().startsWith("/startInstance");
    }

    @Override
    public List<Object> handle(Update update, ChatEntity chat) {
        cloudService.sendRequest("StartInstances",
                HttpMethodName.GET,
                Map.of("InstanceId.1", "i-929EDD20"),
                null);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat.getId());
        sendMessage.setText("Готово!");
        return List.of(sendMessage);
    }

    @Override
    public String description() {
        return "/startInstance";
    }
}
