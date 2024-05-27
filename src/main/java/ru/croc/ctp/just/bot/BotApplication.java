package ru.croc.ctp.just.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

@SpringBootApplication
@Import(TelegramBotStarterConfiguration.class)
public class BotApplication {

    public static void main(String[] args) {
        ApplicationModules.of(BotApplication.class).verify();
        SpringApplication.run(BotApplication.class, args);
    }

}
