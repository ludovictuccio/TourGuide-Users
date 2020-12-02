package com.tourGuide.users;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import tripPricer.TripPricer;

@EnableFeignClients
@SpringBootApplication
public class TourGuideUsersApplication {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        SpringApplication.run(TourGuideUsersApplication.class, args);
    }

    @Bean
    public TripPricer getTripPricer() {
        return new TripPricer();
    }

}
