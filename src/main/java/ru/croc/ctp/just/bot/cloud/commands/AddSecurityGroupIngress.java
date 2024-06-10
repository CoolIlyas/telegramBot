package ru.croc.ctp.just.bot.cloud.commands;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.StaxResponseHandler;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.transform.DescribeSecurityGroupsResultStaxUnmarshaller;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.croc.ctp.just.bot.cloud.service.CloudService;
import ru.croc.ctp.just.bot.telegram.ChatEntity;
import ru.croc.ctp.just.bot.telegram.Command;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static ru.croc.ctp.just.bot.telegram.BotUtil.sendMessage;

/**
 * Команда на добавление ip в белый лист SecurityGroup.
 */
@Component
@RequiredArgsConstructor
public class AddSecurityGroupIngress implements Command {
    /**
     * Сервис для отправки запросов в облако.
     */
    private final CloudService cloudService;
    /**
     * Id SecurityGroup облака.
     */
    @Value("${cloud.securityGroupId}")
    private String securityGroupId;

    /**
     * Постфикс для ip.
     */
    private final String IP_MASK_POSTFIX = "/32";

    /**
     * Префикс для описания правила.
     */
    private final String USERNAME_PREFIX = "t_";

    @Override
    public boolean isCalled(Update update) {
        return update.getMessage().hasText() && update.getMessage().getText().startsWith("/addIp");
    }

    @Override
    public List<Object> handle(Update update, ChatEntity chat) {
        // Распознаем ip
        String text = update.getMessage().getText();
        String[] splitedText = text.split(" ");
        if (splitedText.length != 2 || !InetAddressValidator.getInstance().isValid(splitedText[1])) {
            return sendMessage("Не удалось распознать ip", chat);
        }
        String ip = splitedText[1];
        //Получаем все правила
        DescribeSecurityGroupsResult result;
        try {
            result= cloudService.sendRequest("DescribeSecurityGroups",
                    HttpMethodName.GET, Map.of("GroupId.1", securityGroupId),
                    new StaxResponseHandler<>(new DescribeSecurityGroupsResultStaxUnmarshaller())).getResult();
        } catch (Exception e) {
            return sendMessage("Произошла ошибка при отправке запроса", chat);
        }
        if (result.getSecurityGroups().isEmpty()) {
            return sendMessage("Произошла ошибка", chat);
        }
        SecurityGroup securityGroup = result.getSecurityGroups().get(0);
        String userName = update.getMessage().getFrom().getUserName();

        //Если уже есть правило для данного пользователя, то нужно его удалить
        Optional<IpPermission> oldIpPermission = securityGroup.getIpPermissions()
                .stream()
                .filter(ipPermission -> Objects.equals(
                        ipPermission.getIpv4Ranges().get(0).getDescription(), USERNAME_PREFIX + userName))
                .findFirst();
        if (oldIpPermission.isPresent()) {
            try {
                String oldId = oldIpPermission.get().getIpv4Ranges().get(0).getCidrIp();
                cloudService.sendRequest("RevokeSecurityGroupIngress", HttpMethodName.GET,
                        Map.of(
                                "GroupId", securityGroupId,
                                "IpPermissions.0.IpProtocol", "-1",
                                "IpPermissions.0.FromPort", "-1",
                                "IpPermissions.0.ToPort", "-1",
                                "IpPermissions.0.IpRanges.1.CidrIp", oldId,
                                "IpPermissions.0.IpRanges.1.Description", USERNAME_PREFIX + userName
                        ), null);
            } catch (Exception e) {
                return sendMessage("Произошла ошибка при удалении старого правила", chat);
            }
        }

        //Добавляем новое правило
        try {
            cloudService.sendRequest("AuthorizeSecurityGroupIngress", HttpMethodName.GET,
                    Map.of(
                            "GroupId", securityGroupId,
                            "IpPermissions.0.IpProtocol", "-1",
                            "IpPermissions.0.FromPort", "-1",
                            "IpPermissions.0.ToPort", "-1",
                            "IpPermissions.0.IpRanges.1.CidrIp", ip + IP_MASK_POSTFIX,
                            "IpPermissions.0.IpRanges.1.Description", USERNAME_PREFIX + userName
                    ), null);
        } catch (AmazonServiceException e) {
            String errorCode = e.getErrorCode();
            String responseText = switch (errorCode) {
                case "InvalidPermission.Duplicate" -> "Правило с такими параметрами уже существует";
                case "RulesPerSecurityGroupLimitExceeded" -> "Превышен лимит правил. Обратитесь к администратору";
                default -> "Произошла ошибка при добавлении нового правила";
            };
            return sendMessage(responseText, chat);
        }
        catch (Exception e) {
            return sendMessage("Произошла ошибка при добавлении правила", chat);
        }
        return sendMessage("Готово!", chat);
    }

    @Override
    public String description() {
        return "/addIp <ip> - добавить ip в белый лист облака";
    }
}
