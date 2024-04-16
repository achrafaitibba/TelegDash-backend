package net.techbridges.telegdash.telegdashTelethonClientGateway;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;




@RestController
@RequestMapping("/count")
@RequiredArgsConstructor
public class ApiGateway {
    @Value("${api.base_url}")
    private String BASEURL;
    @Value("${api.auth.username}")
    private String username;
    @Value("${api.auth.password}")
    private String password;
    private final RestTemplate restTemplate;





}
