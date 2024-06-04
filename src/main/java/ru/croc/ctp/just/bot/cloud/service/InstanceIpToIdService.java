package ru.croc.ctp.just.bot.cloud.service;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.StaxResponseHandler;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.transform.DescribeInstancesResultStaxUnmarshaller;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для хранения мапы id-ip экземпляров.
 */
@Service
@RequiredArgsConstructor
public class InstanceIpToIdService {
    /**
     * Мара id-ip.
     */
    private Map<String, String> ipIdMap;
    /**
     * Сервис для отправки запросов в облако.
     */
    private final CloudService cloudService;

    /**
     * Инициализация мапы при запуске приложения.
     */
    @PostConstruct
    private void init() {
        refreshMap();
    }

    /**
     * Возвращает id экземпляра.
     * @param ip ip экземпляра
     * @return id экземпляра
     */
    public String getId(String ip) {
        return ipIdMap.get(ip);
    }

    /**
     * Метод для обновления мапы.
     */
    public void refreshMap() {
        DescribeInstancesResult result =  cloudService.sendRequest("DescribeInstances",
                HttpMethodName.GET, null,
                new StaxResponseHandler<>(new DescribeInstancesResultStaxUnmarshaller())).getResult();
        ipIdMap = result.getReservations().stream()
                .map(Reservation::getInstances)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Instance::getPrivateIpAddress, Instance::getInstanceId));
    }
}
