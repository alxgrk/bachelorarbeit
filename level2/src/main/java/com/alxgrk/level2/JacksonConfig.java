/*
 * Created on May 23, 2017
 *
 * author age
 */
package com.alxgrk.level2;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    // TODO evaluate if this is really necessary

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json()
                .indentOutput(true)
                .failOnUnknownProperties(false)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return builder;
    }
}
