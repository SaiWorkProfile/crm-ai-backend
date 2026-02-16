package com.realestate.ai.config;

import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

@Bean
public WebClient.Builder webClientBuilder(){
return WebClient.builder();
}

}
