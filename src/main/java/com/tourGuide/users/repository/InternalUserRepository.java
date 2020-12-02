package com.tourGuide.users.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.VisitedLocation;

@Repository
public class InternalUserRepository {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InternalUserRepository.class);

    /**********************************************************************************
     * 
     * Methods Below: For Internal Testing
     * 
     **********************************************************************************/

    // Database connection will be used for external users, but for testing
    // purposes internal users are provided and stored in memory
    public final Map<String, User> internalUserMap = new HashMap<>();

    /**
     * Method used to create users tests. Must set InternalTestHelper to modify
     * users test number.
     */
    public void initializeInternalUsers() {
        LOGGER.info("TestMode enabled");
        LOGGER.debug("Initializing users");
        IntStream.range(0, InternalTestHelper.getInternalUserNumber())
                .forEach(i -> {
                    String userName = "internalUser" + i;
                    String phone = "000";
                    String email = userName + "@tourGuide.com";
                    User user = new User(UUID.randomUUID(), userName, phone,
                            email);
                    generateUserLocationHistory(user);

                    internalUserMap.put(userName, user);
                });
        LOGGER.debug("Created " + InternalTestHelper.getInternalUserNumber()
                + " internal test users.");
        LOGGER.debug("Finished initializing users");
    }

    public static void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(
                    new VisitedLocation(user.getUserId(),
                            new Location(generateRandomLatitude(),
                                    generateRandomLongitude()),
                            getRandomTime()));
        });
    }

    private static double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private static double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private static Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now()
                .minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}
