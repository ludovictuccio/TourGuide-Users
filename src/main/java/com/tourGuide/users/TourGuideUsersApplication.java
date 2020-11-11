package com.tourGuide.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import gpsUtil.GpsUtil;

//@EnableFeignClients
@SpringBootApplication
public class TourGuideUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourGuideUsersApplication.class, args);
    }

    @Bean
    public GpsUtil getGpsUtil() {
        return new GpsUtil();
    }

}
