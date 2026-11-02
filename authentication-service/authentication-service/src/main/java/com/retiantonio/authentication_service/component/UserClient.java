package com.retiantonio.authentication_service.component;

import com.retiantonio.authentication_service.dto.request.CreateUserRequest;
import com.retiantonio.authentication_service.dto.response.ProfileResponse;
import com.retiantonio.authentication_service.exception.AlreadyTakenException;
import com.retiantonio.authentication_service.exception.RemoteServiceException;
import com.retiantonio.authentication_service.exception.ServiceUnavailableException;
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
public class UserClient {

    private final RestClient restClient;

    public UserClient(@Value("${services.user.url}") String baseUrl,
                      @Value("${internal.api-key}") String internalKey) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2)).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Internal-Key", internalKey)
                .requestFactory(factory)
                .build();
    }

    public UUID createUser(CreateUserRequest profileRequest) {
        try {
            ProfileResponse created = restClient.post()
                    .uri("/users").body(profileRequest)
                    .retrieve().body(ProfileResponse.class);

            return created.uuid();
        } catch (HttpClientErrorException.Conflict e) {
            throw new AlreadyTakenException("E-mail is already taken.");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Profile data rejected by user service.");
        } catch (RestClientResponseException e) {
            throw new RemoteServiceException("User service failed: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("User service is unavailable");
        }
    }
}
