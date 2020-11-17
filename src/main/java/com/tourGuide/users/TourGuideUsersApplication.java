package com.tourGuide.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import com.tourGuide.users.repository.InternalUserRepository;

@EnableFeignClients
@SpringBootApplication
public class TourGuideUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourGuideUsersApplication.class, args);
    }

    @Bean
    public InternalUserRepository getInternalUserRepository() {
        return new InternalUserRepository();
    }

}
