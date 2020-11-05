package com.tourGuide.users.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.helper.InternalTestHelper;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;

@SpringJUnitConfig(value = UserService.class)
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
        gpsUtil = new GpsUtil();
        userService = new UserService(gpsUtil);
        // rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
    }

    @Test
    @DisplayName("User Location")
    public void getUserLocation() {
        // GIVEN

        // WHEN
        VisitedLocation visitedLocation = userService.trackUserLocation(user);
        userService.tracker.stopTracking();

        // THEN
        assertThat(visitedLocation.userId).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("Add User")
    public void addUser() {
        // GIVEN
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        User retrivedUser = userService.getUser(user.getUserName());
        User retrivedUser2 = userService.getUser(user2.getUserName());
        userService.tracker.stopTracking();

        // THEN
        assertThat(user).isEqualTo(retrivedUser);
        assertThat(user2).isEqualTo(retrivedUser2);
    }

    @Test
    @DisplayName("Get All Users")
    public void getAllUsers() {
        // GIVEN
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        List<User> allUsers = userService.getAllUsers();
        userService.tracker.stopTracking();

        // THEN
        assertThat(allUsers.contains(user)).isTrue();
        assertThat(allUsers.contains(user2)).isTrue();
    }

    @Test
    @DisplayName("Track User")
    public void trackUser() {
        // GIVEN

        // WHEN
        VisitedLocation visitedLocation = userService.trackUserLocation(user);
        userService.tracker.stopTracking();

        // THEN
        assertThat(user.getUserId()).isEqualTo(visitedLocation.userId);
    }

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
