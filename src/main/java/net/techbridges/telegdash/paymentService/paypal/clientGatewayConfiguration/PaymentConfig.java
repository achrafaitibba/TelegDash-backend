package net.techbridges.telegdash.paymentService.paypal.clientGatewayConfiguration;

import net.techbridges.telegdash.paymentService.paypal.model.BaseUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Value("${payment.paypal.api.base-url}")
    private String basUrl;


    @Bean
    public BaseUrl baseUrl(){
        BaseUrl baseUrl = new BaseUrl();
        baseUrl.setBaseUrl(basUrl);
        return baseUrl;
    }

//    @Bean
//    public HttpHeaders httpHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.setBasicAuth(clientId, secret);
//        //headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes()));
//        return headers;
//    }





}