package ru.croc.ctp.just.bot.cloud.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.DefaultErrorResponseHandler;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.transform.LegacyErrorUnmarshaller;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Сервис для отправки запросов в облако.
 */
@Service
public class CloudService {
    /**
     * Адрес облака.
     */
    @Value("${cloud.endpoint}")
    private String cloudEndpoint;

    /**
     * Регион.
     */
    @Value("${cloud.region}")
    private String region;

    /**
     * AccessKey пользователя.
     */
    @Value("${cloud.accessKey}")
    private String accessKey;

    /**
     * SecretKey пользователя.
     */
    @Value("${cloud.secretKey}")
    private String secretKey;

    /**
     * Метод для отправки сообщения в облако.
     * @param action Название операции
     * @param httpMethod Http метод запроса
     * @param parameters параметры запроса
     * @param handler хендлер для обработки ответа с облака. Если обрабатывать ответ не нужно, то может быть null
     * @return возвращает дженерик хендлера, если хендлер = null, то вернется null
     * @param <T> дженерик хендлера, может быть чем угодно
     */
    public <T> T sendRequest(@NotBlank String action,
                            @NotNull HttpMethodName httpMethod,
                            Map<String, String> parameters,
                            HttpResponseHandler<T> handler) {
        //Instantiate the request
        Request<Void> request = new DefaultRequest<>(""); //Request to ElasticSearch
        request.setHttpMethod(httpMethod);
        request.setEndpoint(URI.create(cloudEndpoint));
        request.addParameter("Action", action);

        if (parameters != null) {
            parameters.forEach(request::addParameter);
        }

        //Sign it...
        AWS4Signer signer = new AWS4Signer();
        signer.setRegionName(region);
        signer.setServiceName(request.getServiceName());
        signer.sign(request, new BasicAWSCredentials(accessKey, secretKey));

        //Execute it and get the response...
        return new AmazonHttpClient(new ClientConfiguration())
                .requestExecutionBuilder()
                .executionContext(new ExecutionContext(true))
                .request(request)
                .errorResponseHandler(new DefaultErrorResponseHandler(List.of(new LegacyErrorUnmarshaller())))
                .execute(handler)
                .getAwsResponse();
    }

}
