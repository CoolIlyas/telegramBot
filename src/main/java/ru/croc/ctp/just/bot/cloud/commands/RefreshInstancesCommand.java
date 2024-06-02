package ru.croc.ctp.just.bot.cloud.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.cloud.service.InstanceIpToIdService;
import ru.croc.ctp.just.bot.telegram.ChatEntity;
import ru.croc.ctp.just.bot.telegram.Command;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RefreshInstancesCommand implements Command {
    private final InstanceIpToIdService instanceIpToIdService;

    @Override
    public boolean isCalled(Update update) {
        return update.getMessage().hasText() && update.getMessage().getText().equals("/refreshInstances");
    }

    @Override
    public List<Object> handle(Update update, ChatEntity chat) {
        instanceIpToIdService.refreshMap();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat.getId());
        sendMessage.setText("Готово!");
        return List.of(sendMessage);
    }

    @Override
    public String description() {
        //скрытая команда
        return null;
    }
}
