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

@Service
@RequiredArgsConstructor
public class InstanceIpToIdService {
    private Map<String, String> ipIdMap;
    private final CloudService cloudService;

    @PostConstruct
    private void init() {
        refreshMap();
    }

    public String getId(String ip) {
        return ipIdMap.get(ip);
    }

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
