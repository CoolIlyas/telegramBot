package ru.croc.ctp.just.bot.telegram;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

/**
 * Сущность чата.
 */
@Getter
@Setter
@Entity
public class ChatEntity {

    @Id
    private Long id;

    @NotNull
    private String title;

    boolean isGroupChat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatEntity chat = (ChatEntity) o;
        return getId() != null && Objects.equals(getId(), chat.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
