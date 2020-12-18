package com.tourGuide.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.AttractionDto;
import com.tourGuide.users.proxies.MicroserviceGpsProxy;
import com.tourGuide.users.services.UserService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test-10_000-users.properties")
//@TestPropertySource(locations = "classpath:application-test-100_000-users.properties")
public class TestPerformanceIT {

    @Autowired
    public MicroserviceGpsProxy microserviceGpsProxy;

    @Autowired
    private UserService userService;

    static {
        Locale.setDefault(Locale.US);
    }

    private static int visitedLocationsCounter = 0;
    private static int rewardsAttributionCounter = 0;

    /*
     * 
     * Before launch this tests, you must :
     * 
     * - Set the boolean isPerformanceTestMode to true, in UserService class (to
     * disable the running tracker in the background)
     * 
     * - Launch GPS and Rewards microservices
     * 
     * If you want change the number of users to test, change the value in the
     * correct application file properties, defined with @TestPropertySource.
     * 
     */

    @Test
    @DisplayName("TRACK LOCATIONS & REWARDS - Tracker simulation with 100,000 users")
    public void trackerHighVolumeTest() throws InterruptedException {
        AttractionDto attraction = microserviceGpsProxy.getAllAttractions()
                .get(0);
        List<User> allUsers = userService.getAllUsers();

        allUsers.forEach(u -> {
            u.clearVisitedLocations();
            u.getUserRewards().clear();
            u.addToVisitedLocations(new VisitedLocation(u.getUserId(),
                    attraction.getLocation(), new Date()));
        });
        System.out.println("Begin tracking for: " + allUsers.size()
                + " users in perfomance IT (locations & rewards).");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        allUsers.forEach(u -> {
            userService.trackUserLocation(u);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(
                "All asynchronous methods have been started. Waiting all responses.");

        while (visitedLocationsCounter < allUsers.size()) {
            visitedLocationsCounter = 0;
            allUsers.forEach(u -> {
                if (u.getVisitedLocations().size() > 1) {
                    visitedLocationsCounter++;
                }
            });
        }
        System.out
                .println("VisitedLocations added: " + visitedLocationsCounter);

        while (rewardsAttributionCounter < allUsers.size()) {
            rewardsAttributionCounter = 0;
            allUsers.forEach(u -> {
                if (u.getUserRewards().size() > 0) {
                    rewardsAttributionCounter++;
                }
            });
        }
        System.out.println("Rewards added: " + rewardsAttributionCounter);
        stopWatch.stop();
        System.out.println("All asynchronous methods completed.");
        System.out.println(
                "Integration Test Performance --> highVolumeTrackLocation: "
                        + allUsers.size() + " users tracked in Time: "
                        + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
                        + " seconds.");

        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS
                .toSeconds(stopWatch.getTime()));

        allUsers.forEach(u -> {
            // 1, or 2 if tracked on attraction location
            assertThat(u.getUserRewards().size()).isGreaterThan(0);
            assertThat(u.getUserRewards().size()).isLessThan(3);

            assertThat(u.getVisitedLocations().size()).isEqualTo(2);
        });
    }

    @Test
    @DisplayName("GET REWARDS - Users should be incremented up to 100,000, and test finishes within 20 minutes")
    public void highVolumeGetRewards() {
        AttractionDto attraction = microserviceGpsProxy.getAllAttractions()
                .get(0);
        List<User> allUsers = userService.getAllUsers();

        allUsers.forEach(u -> {
            u.clearVisitedLocations();
            u.getUserRewards().clear();
            u.addToVisitedLocations(new VisitedLocation(u.getUserId(),
                    attraction.getLocation(), new Date()));
        });
        System.out.println("Begin track rewards of: " + allUsers.size()
                + " users in perfomance IT.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        allUsers.forEach(u -> {
            userService.trackUserRewards(u);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(
                "All asynchronous methods have been started. Waiting all responses.");

        while (rewardsAttributionCounter < allUsers.size()) {
            rewardsAttributionCounter = 0;
            allUsers.forEach(u -> {
                if (u.getUserRewards().size() > 0) {
                    rewardsAttributionCounter++;
                }
            });
        }
        System.out.println("Rewards added: " + rewardsAttributionCounter);
        stopWatch.stop();

        System.out.println(
                "TRACK REWARDS - Integration Test Performance --> highVolumeGetRewards: "
                        + allUsers.size() + " users tracked in Time: "
                        + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
                        + " seconds.");

        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS
                .toSeconds(stopWatch.getTime()));
        allUsers.forEach(u -> {
            assertThat(u.getUserRewards().size()).isEqualTo(1);
        });
    }

}
