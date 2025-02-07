package net.techbridges.telegdash.telegdashTelethonClientGateway.service;

import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${api.auth.username}")
    private String username;
    @Value("${api.auth.password}")
    private String password;
    private final HttpHeaders httpHeaders;

    public HttpHeaders authenticate() {
        httpHeaders.setBasicAuth(username, password);
        return httpHeaders;
    }

}