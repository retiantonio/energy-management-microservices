package com.retiantonio.user_service.component;

import com.retiantonio.user_service.exception.RemoteServiceException;
import com.retiantonio.user_service.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;

@Component
public class DeviceClient {

    private final RestClient restClient;

    public DeviceClient(@Value("${services.device.url}") String baseUrl,
                        @Value("${internal.api-key}") String internalKey) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Internal-Key", internalKey)
                .requestFactory(factory)
                .build();
    }

    public void unassignAllFrom(UUID userId) {
        try {
            restClient.delete()
                    .uri("/devices/user/{userId}/assignments", userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            // device-service answered, but with an error (4xx or 5xx)
            throw new RemoteServiceException("Device service failed: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            // connection refused or timeout
            throw new ServiceUnavailableException("Device service is unavailable");
        }
    }
}