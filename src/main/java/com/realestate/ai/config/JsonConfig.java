package com.realestate.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;

@Configuration
public class JsonConfig {

@Bean
public ObjectMapper objectMapper(){
return new ObjectMapper();
}

}
