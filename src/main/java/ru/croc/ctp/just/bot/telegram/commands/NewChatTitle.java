package ru.croc.ctp.just.bot.telegram.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.telegram.ChatEntity;
import ru.croc.ctp.just.bot.telegram.Command;
import ru.croc.ctp.just.bot.telegram.repo.ChatRepository;

import java.util.List;
import java.util.Objects;

/**
 * Команда реагирует на изменения названия чатов и обновляет запись в бд.
 */
@Component
@RequiredArgsConstructor
public class NewChatTitle implements Command {
    private final ChatRepository repository;

    @Override
    public boolean isCalled(Update update) {
        return Objects.nonNull(update.getMessage().getNewChatTitle());
    }

    @Override
    public List<Object> handle(Update update, ChatEntity chat) {
        chat.setTitle(update.getMessage().getNewChatTitle());
        repository.save(chat);
        return null;
    }

    @Override
    public String description() {
        return null;
    }
}
