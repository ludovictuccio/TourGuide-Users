package com.tourGuide.users.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
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
import com.tourGuide.users.domain.UserPreferences;
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

    @Test
    @Tag("addUser_getAllUsernames")
    @DisplayName("getAllUsernames")
    public void givenTwoUser_whenGetAllUsernames_thenReturnTwo() {
        // GIVEN
        userService = new UserService(gpsUtil);

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        List<String> allUsers = userService.getAllUsernames();
        userService.tracker.stopTracking();

        // THEN
        assertThat(allUsers.size()).isEqualTo(2);
        assertThat(allUsers.contains(user.getUserName())).isTrue();
        assertThat(allUsers.contains(user2.getUserName())).isTrue();
    }

    @Test
    @Tag("addUser")
    @DisplayName("Add User - Ok - Different username")
    public void givenTwoUsers_whenAddNewUserWithDifferentUsername_thenReturnAdded() {
        // GIVEN
        userService = new UserService(gpsUtil);

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        boolean result = userService.addUser(new User(UUID.randomUUID(),
                "newUser", "123456789", "email@email.com"));
        userService.tracker.stopTracking();

        List<String> allUsers = userService.getAllUsernames();

        // THEN
        assertThat(result).isTrue();
        assertThat(allUsers.size()).isEqualTo(3);
    }

    @Test
    @Tag("addUser")
    @DisplayName("Add User - Already existing username")
    public void givenTwoUsers_whenAddNewUserWithExistingUsername_thenReturnNotAdded() {
        // GIVEN
        userService = new UserService(gpsUtil);

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        boolean result = userService.addUser(user);
        userService.tracker.stopTracking();

        List<String> allUsers = userService.getAllUsernames();

        // THEN
        assertThat(result).isFalse();
        assertThat(allUsers.size()).isEqualTo(2);
    }

    @Test
    @Tag("addUser")
    @DisplayName("Add User - Empty username")
    public void givenTwoUsers_whenAddNewUserWithEmptyUsername_thenReturnNotAdded() {
        // GIVEN
        userService = new UserService(gpsUtil);

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        boolean result = userService.addUser(new User(UUID.randomUUID(), "",
                "123456789", "email@email.com"));
        userService.tracker.stopTracking();

        List<String> allUsers = userService.getAllUsernames();

        // THEN
        assertThat(result).isFalse();
        assertThat(allUsers.size()).isEqualTo(2);
    }

    @Test
    @Tag("getUser")
    @DisplayName("Get User - Ok - Valid username")
    public void givenOneUser_whenGetWithValidUserName_thenReturnNotNull() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        userService.addUser(user);

        // WHEN
        User result = userService.getUser("jon");
        userService.tracker.stopTracking();

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getEmailAddress()).isEqualTo("jon@tourGuide.com");
        assertThat(result.getPhoneNumber()).isEqualTo("000");
    }

    @Test
    @Tag("getUser")
    @DisplayName("Get User - Error - Invalid username")
    public void givenOneUser_whenGetWithInvalidUserName_thenReturnNull() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        userService.addUser(user);

        // WHEN
        User result = userService.getUser("unknow");
        userService.tracker.stopTracking();

        // THEN
        assertThat(result).isNull();
    }

    @Test
    @Tag("updateUserPreferences")
    @DisplayName("Update UserPreferences - Ok - Valid username")
    public void givenUser_whenUpdatePreferences_thenReturnUpdated() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        userService.addUser(user);

        assertThat(user.getUserPreferences().getTripDuration()).isEqualTo(1);
        assertThat(user.getUserPreferences().getTicketQuantity()).isEqualTo(1);
        assertThat(user.getUserPreferences().getNumberOfAdults()).isEqualTo(1);
        assertThat(user.getUserPreferences().getNumberOfChildren())
                .isEqualTo(0);

        UserPreferences userPreferences = new UserPreferences(3, 7, 2, 5);

        // WHEN
        boolean result = userService.updateUserPreferences("jon",
                userPreferences);
        userService.tracker.stopTracking();

        // THEN
        assertThat(result).isTrue();
        assertThat(user.getUserPreferences().getTripDuration()).isEqualTo(3);
        assertThat(user.getUserPreferences().getTicketQuantity()).isEqualTo(7);
        assertThat(user.getUserPreferences().getNumberOfAdults()).isEqualTo(2);
        assertThat(user.getUserPreferences().getNumberOfChildren())
                .isEqualTo(5);
    }

    @Test
    @Tag("updateUserPreferences")
    @DisplayName("Update UserPreferences - ERROR - Invalid username")
    public void givenInvalidUsername_whenUpdatePreferences_thenReturnNotUpdated() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        userService.addUser(user);

        UserPreferences userPreferences = new UserPreferences(3, 7, 2, 5);

        // WHEN
        boolean result = userService.updateUserPreferences("UNKNOW",
                userPreferences);
        userService.tracker.stopTracking();

        // THEN
        assertThat(result).isFalse();
        assertThat(user.getUserPreferences().getTripDuration()).isEqualTo(1);
        assertThat(user.getUserPreferences().getTicketQuantity()).isEqualTo(1);
        assertThat(user.getUserPreferences().getNumberOfAdults()).isEqualTo(1);
        assertThat(user.getUserPreferences().getNumberOfChildren())
                .isEqualTo(0);
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
