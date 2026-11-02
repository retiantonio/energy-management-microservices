package com.retiantonio.device_service.component;

import com.retiantonio.device_service.exception.RemoteServiceException;
import com.retiantonio.device_service.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(@Value("${services.user.url}") String baseUrl,
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

    public boolean exists(UUID userId) {
        try {
            List<?> users = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users")
                            .queryParam("id", userId)
                            .build())
                    .retrieve()
                    .body(List.class);

            return users != null && !users.isEmpty();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (RestClientResponseException e) {
            throw new RemoteServiceException("User service failed: " + e.getStatusCode() + ".");
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("User service is unavailable.");
        }
    }
}