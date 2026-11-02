package com.retiantonio.user_service.component;

import com.retiantonio.user_service.dto.request.CreateCredentialRequest;
import com.retiantonio.user_service.dto.request.UpdateCredentialRequest;
import com.retiantonio.user_service.exception.AlreadyTakenException;
import com.retiantonio.user_service.exception.RemoteServiceException;
import com.retiantonio.user_service.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;

@Component
public class CredentialClient {

    private final RestClient restClient;

    public CredentialClient(@Value("${services.credential.url}") String baseUrl,
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

    public void createCredential(CreateCredentialRequest request) {
        try {
            restClient.post()
                    .uri("/credentials")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            throw new AlreadyTakenException("Username already taken.");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Credential data rejected by auth service.");
        } catch (RestClientResponseException e) {
            throw new RemoteServiceException("Credential service failed: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("Credential service is unavailable");
        }
    }

    public void updateCredentialFor(UUID userId, UpdateCredentialRequest request) {
        try {
            restClient.put()
                    .uri("/credentials/{userId}", userId)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            throw new AlreadyTakenException("Username already taken.");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Credential data rejected by auth service.");
        } catch (RestClientResponseException e) {
            throw new RemoteServiceException("Credential service failed: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("Credential service is unavailable");
        }
    }

    public void deleteCredentialFrom(UUID userId) {
        try {
            restClient.delete()
                    .uri("/credentials/{userId}", userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            // user-service answered, but with an error (4xx or 5xx)
            throw new RemoteServiceException("Credential service failed: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            // connection refused or timeout
            throw new ServiceUnavailableException("Credential service is unavailable");
        }
    }
}
