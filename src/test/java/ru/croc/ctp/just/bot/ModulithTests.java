package ru.croc.ctp.just.bot;

import javafx.application.Application;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModulithTests {
    @Test
    void createModuleDocumentation() {
        ApplicationModules modules = ApplicationModules.of(BotApplication.class);
        new Documenter(modules).writeModulesAsPlantUml();
    }

    @Test
    void verifiesModularStructure() {
        ApplicationModules modules = ApplicationModules.of(BotApplication.class).verify();
        modules.verify();
    }
}
