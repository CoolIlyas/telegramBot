package ru.croc.ctp.just.bot.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;
import static org.springframework.util.ResourceUtils.FILE_URL_PREFIX;

/**
 * Сервис для авторизации пользователей.
 */
@Service
public class SecurityService {
    private Set<String> validUsers = new HashSet<>();

    private final String filePath;

    /**
     * Конструктор.
     * @param filePath путь до файла
     */
    public SecurityService(@Value("${security.filePath:classpath:/validUsers.txt}") String filePath) {
        this.filePath = filePath.startsWith(CLASSPATH_URL_PREFIX) ? filePath : FILE_URL_PREFIX + Path.of(filePath);
    }

    @PostConstruct
    private void setup() throws IOException {
        refreshUserSet();
    }

    /**
     * Метод обновляет данные о разрешенных пользователях из файла.
     */
    public void refreshUserSet() throws IOException {
        Resource resource =  ResourcePatternUtils
                .getResourcePatternResolver(new DefaultResourceLoader()).getResource(filePath);
        String content = resource.getContentAsString(StandardCharsets.UTF_8);
        String[] users = content.split("\n");
        validUsers = new HashSet<>(Arrays.stream(users).map(String::strip).toList());
    }

    /**
     * Метод проверяет, является ли пользователь разрешенным.
     * @param userName имя пользователя, которое нужно проверить.
     * @return true - пользователь валидный, false - невалидный
     */
    public boolean isUserValid(String userName) {
        return validUsers.contains(userName);
    }
}
