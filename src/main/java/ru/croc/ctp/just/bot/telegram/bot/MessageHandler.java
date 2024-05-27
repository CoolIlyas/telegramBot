package ru.croc.ctp.just.bot.telegram.bot;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.telegram.Command;
import ru.croc.ctp.just.bot.telegram.ChatEntity;
import ru.croc.ctp.just.bot.telegram.CommandInProgress;
import ru.croc.ctp.just.bot.telegram.CommandWithStatus;
import ru.croc.ctp.just.bot.telegram.repo.ChatRepository;
import ru.croc.ctp.just.bot.telegram.repo.CommandInProgressRepository;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Обработчик сообщений.
 */
@Component
public class MessageHandler {
    private final List<Command> commands;

    private final ChatRepository chatRepository;

    private final CommandInProgressRepository commandInProgressRepository;

    private final String helpText;

    /**
     * Конструктор.
     * @param commands все бины реализующие интерфейс Command
     * @param chatRepository репозиторий сущности ChatEntity
     * @param commandInProgressRepository репозиторий сущности CommandInProgress
     */
    public  MessageHandler(List<Command> commands,
                           ChatRepository chatRepository,
                           CommandInProgressRepository commandInProgressRepository) {
        this.commands = commands;
        this.chatRepository = chatRepository;
        helpText = commands.stream()
                .map(Command::description)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
        this.commandInProgressRepository = commandInProgressRepository;
    }

    @PostConstruct
    private void deleteAllCommands(){
        commandInProgressRepository.deleteAll();
    }

    /**
     * Обработка сообщения.
     * @param update сообщение от юзера
     * @return лист с ответами, которые отправит бот.
     */
    @Transactional
    public List<Object> handle(Update update) {
        //Получаем чат
        ChatEntity chat = getChat(update);

        if (chat == null) {
            return List.of();
        }

        //Если есть активная команда для этого чата, то отправляем сразу туда
        CommandInProgress commandInProgresses = commandInProgressRepository.findByChat(chat);
        if (commandInProgresses != null) {
             return commands.stream()
                     .filter(c -> c.getClass().equals(commandInProgresses.getCommandClass()))
                     .findFirst()
                     .filter(CommandWithStatus.class::isInstance)
                     .map(CommandWithStatus.class::cast)
                     .map(command -> command.handleCommandWithStatus(update, commandInProgresses))
                     .orElse(List.of());
        }

        //Проверяем что не /help
        if (isHelp(update)) {
            return buildHelpAnswer(update);
        }
        return commands.stream()
                .filter(command -> command.isCalled(update))
                .findFirst()
                .map(command -> command.handle(update, chat))
                .orElse(List.of());
    }


    /**
     * Проверяет не является ли введенное сообщение командой /help.
     * @param update update
     * @return true - является, false - не является
     */
    private boolean isHelp(Update update) {
        return update.getMessage().hasText() && update.getMessage().getText().startsWith("/help");
    }

    /**
     * Возвращает ответ на команду /help.
     * @param update update
     * @return ответ на команду /help
     */
    private List<Object> buildHelpAnswer(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(helpText);
        return List.of(sendMessage);
    }

    /**
     * Получает чат из бд или создает новый.
     * @param update сообщение пользователя
     * @return объект ChatEntity
     */
    private ChatEntity getChat(Update update) {
        Long chatId;
        if (update.getMessage() != null) {
            chatId = update.getMessage().getChatId();
        } else if (update.getCallbackQuery() != null) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            return null;
        }

        Optional<ChatEntity> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            ChatEntity newChatEntity = new ChatEntity();
            newChatEntity.setId(chatId);
            if (update.getMessage().getChat().isGroupChat()) {
                newChatEntity.setGroupChat(true);
                newChatEntity.setTitle(update.getMessage().getChat().getTitle());
            } else {
                newChatEntity.setTitle(update.getMessage().getChat().getUserName());
            }
            return chatRepository.save(newChatEntity);
        } else {
            return chatOpt.get();
        }
    }
}
