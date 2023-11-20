package ca.webdev.exchange;

import ca.webdev.exchange.matching.MatchingEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public MatchingEngine matchingEngine() {
        return new MatchingEngine(0.01, 2);
    }

}
