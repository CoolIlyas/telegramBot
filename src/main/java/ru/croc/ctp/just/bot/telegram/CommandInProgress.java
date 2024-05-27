package ru.croc.ctp.just.bot.telegram;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Команды которые сейчас находятся в процессе.
 */
@Getter
@Setter
@Entity
public class CommandInProgress {

    @Id
    @GeneratedValue
    UUID uuid;

    @NotNull
    @ManyToOne
    @JoinColumn
    ChatEntity chat;

    /**
     * Число, которому соответствует энам статуса данной команды.
     */
    String status;

    /**
     * Класс команды.
     */
    @NotNull
    Class commandClass;

    /**
     * Время и дата создания.
     */
    @NotNull
    LocalDateTime createdTime;

    @ElementCollection
    @CollectionTable(name = "command_attribute_value",
            joinColumns = {@JoinColumn(name = "command_id")})
    @MapKeyColumn(name = "attribute")
    @Column(name = "value", length = 2000)
    private Map<String, String> attributes = new HashMap<>();
}
