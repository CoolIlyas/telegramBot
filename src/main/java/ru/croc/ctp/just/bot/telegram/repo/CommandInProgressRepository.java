package ru.croc.ctp.just.bot.telegram.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.croc.ctp.just.bot.telegram.ChatEntity;
import ru.croc.ctp.just.bot.telegram.CommandInProgress;

import java.util.UUID;

public interface CommandInProgressRepository  extends JpaRepository<CommandInProgress, UUID> {
    CommandInProgress findByChat(ChatEntity chat);
}
