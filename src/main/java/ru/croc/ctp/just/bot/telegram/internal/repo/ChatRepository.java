package ru.croc.ctp.just.bot.telegram.internal.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.croc.ctp.just.bot.telegram.ChatEntity;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> { }