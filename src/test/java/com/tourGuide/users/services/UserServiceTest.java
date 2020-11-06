package com.tourGuide.users.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.helper.InternalTestHelper;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    public UserService userService;

//    @MockBean
//    private RewardsService rewardsService;

    @MockBean
    private GpsUtil gpsUtil;

    private User user;
    private User user2;

    @BeforeEach
    public void setUpPerTest() {
        // gpsUtil = new GpsUtil();
        userService = new UserService(gpsUtil);
        // rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
    }

    @Test
    @Tag("InternalTestHelper")
    @DisplayName("InternalTestHelper - 0 users")
    public void givenZeroInternalUserNumber_whenCheckQuantity_thenReturnZero() {
        // GIVEN
        // WHEN
        // THEN
        assertThat(InternalTestHelper.getInternalUserNumber()).isEqualTo(0);
    }

    @Test
    @Tag("InternalTestHelper")
    @DisplayName("InternalTestHelper - 50 users")
    public void givenFiftyInternalUserNumber_whenCheckQuantity_thenReturnFifty() {
        // GIVEN
        // WHEN
        InternalTestHelper.setInternalUserNumber(50);
        // THEN
        assertThat(InternalTestHelper.getInternalUserNumber()).isEqualTo(50);
    }

    @Test
    @Tag("getUserLocation")
    @DisplayName("User Location - Ok")
    public void givenUser_whenGetLocation_thenReturnOk() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        Location location = new Location(
                ThreadLocalRandom.current().nextDouble(-85.05112878D,
                        85.05112878D),
                ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D));

        user.addToVisitedLocations(
                new VisitedLocation(user.getUserId(), location, new Date()));

        // WHEN
        VisitedLocation result = userService.getUserLocation(user);
        userService.tracker.stopTracking();

        // THEN
        assertThat(result.location.longitude)
                .isEqualTo(user.getLastVisitedLocation().location.longitude);
        assertThat(result.location.latitude)
                .isEqualTo(user.getLastVisitedLocation().location.latitude);
        assertThat(result.timeVisited)
                .isEqualTo(user.getLastVisitedLocation().timeVisited);
        assertThat(result.userId).isEqualTo(user.getUserId());
        verify(gpsUtil, times(0)).getUserLocation(user.getUserId());
    }

    @Test
    @Tag("trackUserLocation")
    @DisplayName("Track User Location - User with empty visited locations size")
    public void givenUserWithoutAlreadyExistingVisitedLocation_whenTrack_thenReturnNewLocation() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        assertThat(user.getVisitedLocations().size()).isEqualTo(0);

        when(gpsUtil.getUserLocation(user.getUserId()))
                .thenReturn(new VisitedLocation(user.getUserId(),
                        new Location(
                                ThreadLocalRandom.current().nextDouble(
                                        -85.05112878D, 85.05112878D),
                                ThreadLocalRandom.current().nextDouble(-180.0D,
                                        180.0D)),
                        new Date()));

        // WHEN
        VisitedLocation result = userService.trackUserLocation(user);
        userService.tracker.stopTracking();

        // THEN
        assertThat(result).isNotNull();
        assertThat(user.getVisitedLocations().size()).isEqualTo(1);
        verify(gpsUtil, times(1)).getUserLocation(user.getUserId());
        assertThat(result.userId).isEqualTo(user.getUserId());
    }

//    @Test
//    @DisplayName("Add User")
//    public void addUser() {
//        // GIVEN
//        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//        user2 = new User(UUID.randomUUID(), "jon2", "000",
//                "jon2@tourGuide.com");
//
//        userService.addUser(user);
//        userService.addUser(user2);
//
//        // WHEN
//        User retrivedUser = userService.getUser(user.getUserName());
//        User retrivedUser2 = userService.getUser(user2.getUserName());
//        userService.tracker.stopTracking();
//
//        // THEN
//        assertThat(user).isEqualTo(retrivedUser);
//        assertThat(user2).isEqualTo(retrivedUser2);
//    }
//
//    @Test
//    @DisplayName("Get All Users")
//    public void getAllUsers() {
//        // GIVEN
//        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//        user2 = new User(UUID.randomUUID(), "jon2", "000",
//                "jon2@tourGuide.com");
//        userService.addUser(user);
//        userService.addUser(user2);
//
//        // WHEN
//        List<User> allUsers = userService.getAllUsers();
//        userService.tracker.stopTracking();
//
//        // THEN
//        assertThat(allUsers.contains(user)).isTrue();
//        assertThat(allUsers.contains(user2)).isTrue();
//    }

//    @Test // Not yet implemented
//    @DisplayName("Get Near by Attractions")
//    public void getNearbyAttractions() {
//        // GIVEN
//        VisitedLocation visitedLocation = userService.trackUserLocation(user);
//
//        // WHEN
//        List<Attraction> attractions = userService
//                .getNearByAttractions(visitedLocation);
//        userService.tracker.stopTracking();
//
//        // THEN
//        assertThat(attractions.size()).isEqualTo(5);
//    }

//    @Test
//    @DisplayName("Get Trip Deals")
//    public void getTripDeals() {
//        // GIVEN
//
//        // WHEN
//        List<Provider> providers = userService.getTripDeals(user);
//        userService.tracker.stopTracking();
//
//        // THEN
//        assertThat(providers.size()).isEqualTo(10);
//    }

}
