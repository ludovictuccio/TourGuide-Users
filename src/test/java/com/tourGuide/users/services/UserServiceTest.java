package com.tourGuide.users.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.UserDto;
import com.tourGuide.users.domain.dto.UserRewardsDto;
import com.tourGuide.users.domain.dto.VisitedLocationDto;
import com.tourGuide.users.proxies.MicroserviceGpsProxy;
import com.tourGuide.users.proxies.MicroserviceRewardsProxy;
import com.tourGuide.users.repository.InternalUserRepository;
import com.tourGuide.users.tracker.Tracker;
import com.tourGuide.users.web.exceptions.InvalidLocationException;

import tripPricer.TripPricer;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    public UserService userService;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @MockBean
    private MicroserviceGpsProxy microserviceGpsProxy;

    @MockBean
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @MockBean
    private TripPricer tripPricer;

    @MockBean
    private Tracker tracker;

    private User user;
    private User user2;
    private User user3;
    private User user4;

    private List<ClosestAttraction> attractionsList;

    private ClosestAttraction tourEiffel;
    private ClosestAttraction louvre;
    private ClosestAttraction lesInvalides;
    private ClosestAttraction lePantheon;
    private ClosestAttraction disneylandParis;

    private static Location userLocation;

    @BeforeEach
    public void setUpPerTest() {
        internalUserRepository.internalUserMap.clear();

        // user in Eiffel Tower location
        userLocation = new Location(48.858331, 2.294481);

        tourEiffel = new ClosestAttraction("Tour Eiffel",
                new Location(48.858331, 2.294481), userLocation, 0.10, 100);
        louvre = new ClosestAttraction("Musée du Louvre",
                new Location(48.861147, 2.338028), userLocation, 1.65, 200);
        lesInvalides = new ClosestAttraction("Hôtel des Invalides",
                new Location(48.853241, 2.312107), userLocation, 2.33, 300);
        lePantheon = new ClosestAttraction("Le Panthéon",
                new Location(48.846012, 2.345924), userLocation, 4.87, 400);
        disneylandParis = new ClosestAttraction("Disneyland Paris",
                new Location(48.872448, 2.776794), userLocation, 21.18, 500);

        attractionsList = new ArrayList<>();
        attractionsList.add(tourEiffel);
        attractionsList.add(louvre);
        attractionsList.add(lesInvalides);
        attractionsList.add(lePantheon);
        attractionsList.add(disneylandParis);
    }

    @Test
    @Tag("getLastVisitedLocation")
    @DisplayName("Get Last VisitedLocation - Ok")
    public void givenUser_whenGetLastLocation_thenReturnLastVisitedLocation() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon1", "111", "jon1@tourGuide.com");
        userService.addUser(user);

        Date date = new Date(2020, 01, 01);
        Date date2 = new Date(2020, 02, 02);

        user.addToVisitedLocations(
                new VisitedLocation(user.getUserId(), new Location(), date));
        user.addToVisitedLocations(
                new VisitedLocation(user.getUserId(), new Location(), date2));

        // WHEN
        VisitedLocation result = userService.getLastVisitedLocation(user);

        // THEN
        assertThat(result.getTimeVisited()).isEqualTo(date2);
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

        // THEN
        assertThat(result.location.longitude).isEqualTo(user
                .getVisitedLocations()
                .get(user.getVisitedLocations().size() - 1).location.longitude);
        assertThat(result.location.latitude).isEqualTo(user
                .getVisitedLocations()
                .get(user.getVisitedLocations().size() - 1).location.latitude);
        assertThat(result.timeVisited).isEqualTo(user.getVisitedLocations()
                .get(user.getVisitedLocations().size() - 1).timeVisited);
        assertThat(result.userId).isEqualTo(user.getUserId());
    }

    @Test
    @Tag("getUserLocation")
    @DisplayName("User Location - Error - No last visited location")
    public void givenUserWithoutVisitedLocation_whenGetLocation_thenReturnError400() {

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        assertThatExceptionOfType(InvalidLocationException.class)
                .isThrownBy(() -> {
                    userService.getUserLocation(user);
                });
    }

    @Test
    @Tag("getAllUsersLocations")
    @DisplayName("All Users Locations - Ok")
    public void givenTwoUsers_whenGetAllLocations_thenReturnAllLocationsValues() {
        // GIVEN
        Location location = new Location(
                ThreadLocalRandom.current().nextDouble(-85.05112878D,
                        85.05112878D),
                ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D));

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "222",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);
        user.addToVisitedLocations(
                new VisitedLocation(user.getUserId(), location, new Date()));
        user2.addToVisitedLocations(
                new VisitedLocation(user.getUserId(), location, new Date()));

        // WHEN
        Map<String, Location> result = userService.getAllUsersLocations();

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @Tag("addUser_getAllUsers")
    @DisplayName("getAllUsers")
    public void givenTwoUser_whenGetAllUser_thenReturnTwo() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        List<User> allUsers = userService.getAllUsers();

        // THEN
        assertThat(allUsers.size()).isEqualTo(2);
    }

    @Test
    @Tag("addUser_getAllUsers")
    @DisplayName("Add User - Ok - Different username")
    public void givenTwoUsers_whenAddNewUserWithDifferentUsername_thenReturnAdded() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        boolean result = userService.addUser(new User(UUID.randomUUID(),
                "newUser", "123456789", "email@email.com"));
        List<User> allUsers = userService.getAllUsers();

        // THEN
        assertThat(result).isTrue();
        assertThat(allUsers.size()).isEqualTo(3);
    }

    @Test
    @Tag("addUser")
    @DisplayName("Add User - Already existing username")
    public void givenTwoUsers_whenAddNewUserWithExistingUsername_thenReturnNotAdded() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        boolean result = userService.addUser(user);

        List<User> allUsers = userService.getAllUsers();

        // THEN
        assertThat(result).isFalse();
        assertThat(allUsers.size()).isEqualTo(2);
    }

    @Test
    @Tag("addUser")
    @DisplayName("Add User - Empty username")
    public void givenTwoUsers_whenAddNewUserWithEmptyUsername_thenReturnNotAdded() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        boolean result = userService.addUser(new User(UUID.randomUUID(), "",
                "123456789", "email@email.com"));

        List<User> allUsers = userService.getAllUsers();

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

        // THEN
        assertThat(result).isNull();
    }

    @Test
    @Tag("getUserDto")
    @DisplayName("Get User Dto- Ok - Valid username")
    public void givenOneUser_whenGetUserDtoWithValidUserName_thenReturnNotNull() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        userService.addUser(user);
        Location location = new Location(
                ThreadLocalRandom.current().nextDouble(-85.05112878D,
                        85.05112878D),
                ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D));
        user.addToVisitedLocations(
                new VisitedLocation(user.getUserId(), location, new Date()));

        // WHEN
        UserDto result = userService.getUserDto("jon");

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @Tag("getUserDto")
    @DisplayName("Get User Dto - Error - Invalid username")
    public void givenOneUser_whenGetUserDtoWithInvalidUserName_thenReturnNullPointerException() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        userService.addUser(user);
        Location location = new Location(
                ThreadLocalRandom.current().nextDouble(-85.05112878D,
                        85.05112878D),
                ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D));
        user.addToVisitedLocations(
                new VisitedLocation(user.getUserId(), location, new Date()));

        // WHEN
        assertThatNullPointerException().isThrownBy(() -> {
            userService.getUserDto("unknow");
        });
    }

    @Test
    @Tag("getUserByUuid")
    @DisplayName("Get User By Uuid - Ok - Valid UUID")
    public void givenOneUser_whenGetUserWithValidId_thenReturnNotNull() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "002",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        Optional<User> result = userService.getUserByUuid(user.getUserId());

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.get().getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @Tag("getUserByUuid")
    @DisplayName("getUserByUuid - Error - Invalid UUID")
    public void givenOneUser_whenGetUserWithInvalidId_thenReturnNotNull()
            throws Exception {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "002",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        assertThatNullPointerException().isThrownBy(() -> {
            userService.getUserByUuid(UUID.randomUUID());
        });
    }

    @Test
    @Tag("getUserRewardsDto")
    @DisplayName("Get UserRewardsDto - Ok - Valid UUID")
    public void givenOneUser_whenGetUserRewardsDtoWithValidId_thenReturnNotNull() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "002",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        UserRewardsDto result = userService.getUserRewardsDto(user.getUserId());

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(user.getUserId());
        assertThat(result.getUserRewards().size()).isEqualTo(0);
        assertThat(result.getVisitedLocations().size()).isEqualTo(0);
    }

    @Test
    @Tag("getUserRewardsDto")
    @DisplayName("Get UserRewardsDto - Error - Invalid UUID")
    public void givenOneUser_whenGetUserRewardsDtoWithInvalidId_thenReturnNotNull()
            throws Exception {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "002",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        assertThatNullPointerException().isThrownBy(() -> {
            userService.getUserRewardsDto(UUID.randomUUID());
        });
    }

    @Test
    @Tag("getAllUsers")
    @DisplayName("Get All Users - Ok - 2 users")
    public void givenTwoUsers_whenGetAllUsers_thenReturnListWithSizeOfTwo() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        List<User> allUsers = userService.getAllUsers();

        // THEN
        assertThat(allUsers.size()).isEqualTo(2);
    }

    @Test
    @Tag("getAllUsers")
    @DisplayName("Get All Users - Ok - 0 users")
    public void givenZeroUsers_whenGetAllUsers_thenReturnEmptyList() {
        // GIVEN

        // WHEN
        List<User> allUsers = userService.getAllUsers();

        // THEN
        assertThat(allUsers.size()).isEqualTo(0);
    }

    @Test
    @Tag("getAllUsersWithVisitedLocations")
    @DisplayName("Get All Users With VisitedLocations - Ok - 4 users, 2 users with VisitedLocation")
    public void givenFourUsersAndTwoWithVisitedLocations_whenGet_thenReturnTwoListSize() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon1", "111", "jon1@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "222",
                "jon2@tourGuide.com");
        user3 = new User(UUID.randomUUID(), "jon3", "333",
                "jon3@tourGuide.com");
        user4 = new User(UUID.randomUUID(), "jon4", "444",
                "jon4@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);
        userService.addUser(user3);
        userService.addUser(user4);

        user3.addToVisitedLocations(new VisitedLocation(user3.getUserId(),
                new Location(), new Date()));
        user4.addToVisitedLocations(new VisitedLocation(user4.getUserId(),
                new Location(), new Date()));

        // WHEN
        List<User> allUsers = userService.getAllUsersWithVisitedLocations();

        // THEN
        assertThat(userService.getAllUsers().size()).isEqualTo(4);
        assertThat(allUsers.size()).isEqualTo(2);
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

        // THEN
        assertThat(result).isFalse();
        assertThat(user.getUserPreferences().getTripDuration()).isEqualTo(1);
        assertThat(user.getUserPreferences().getTicketQuantity()).isEqualTo(1);
        assertThat(user.getUserPreferences().getNumberOfAdults()).isEqualTo(1);
        assertThat(user.getUserPreferences().getNumberOfChildren())
                .isEqualTo(0);
    }

    @Test
    @Tag("getTheFiveClosestAttractions")
    @DisplayName("Get The Five Closest Attractions - Ok - User in Eiffel Tower location")
    public void givenUserInEiffelTowerLocation_whenGetTheFiveClosestAttraction_thenReturnValidAttractions() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Location tourEiffelLocation = new Location(48.858331, 2.294481);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                tourEiffelLocation, new Date()));
        userService.addUser(user);

        when(microserviceGpsProxy.getClosestAttractions(user.getUserName()))
                .thenReturn(attractionsList);

        // WHEN
        List<ClosestAttraction> result = userService
                .getTheFiveClosestAttractions(user.getUserName());

        // THEN
        assertThat(result.size()).isEqualTo(5);

        assertThat(result.get(0).getAttractionName()
                .contains(tourEiffel.getAttractionName())).isTrue();
        assertThat(result.get(1).getAttractionName()
                .contains(louvre.getAttractionName())).isTrue();
        assertThat(result.get(2).getAttractionName()
                .contains(lesInvalides.getAttractionName())).isTrue();
        assertThat(result.get(3).getAttractionName()
                .contains(lePantheon.getAttractionName())).isTrue();
        assertThat(result.get(4).getAttractionName()
                .contains(disneylandParis.getAttractionName())).isTrue();

        assertThat(result.get(0).getAttractionRewardsPoints()).isEqualTo(100);
        assertThat(result.get(1).getAttractionRewardsPoints()).isEqualTo(200);
        assertThat(result.get(2).getAttractionRewardsPoints()).isEqualTo(300);
        assertThat(result.get(3).getAttractionRewardsPoints()).isEqualTo(400);
        assertThat(result.get(4).getAttractionRewardsPoints()).isEqualTo(500);

        assertThat(result.get(0).getDistanceInMiles()).isEqualTo(0.10);
        assertThat(result.get(1).getDistanceInMiles()).isEqualTo(1.65);
        assertThat(result.get(2).getDistanceInMiles()).isEqualTo(2.33);
        assertThat(result.get(3).getDistanceInMiles()).isEqualTo(4.87);
        assertThat(result.get(4).getDistanceInMiles()).isEqualTo(21.18);

        assertThat(result.get(0).getUserLocation()).isEqualTo(userLocation);
        assertThat(result.get(1).getUserLocation()).isEqualTo(userLocation);
        assertThat(result.get(2).getUserLocation()).isEqualTo(userLocation);
        assertThat(result.get(3).getUserLocation()).isEqualTo(userLocation);
        assertThat(result.get(4).getUserLocation()).isEqualTo(userLocation);
    }

    @Test
    @Tag("getTheFiveClosestAttractions")
    @DisplayName("Get The Five Closest Attractions - Error - Invalid username entry")
    public void givenInvalidUsername_whenGetTheFiveClosestAttraction_thenReturnEmptyList() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        when(microserviceGpsProxy.getClosestAttractions(user.getUserName()))
                .thenReturn(attractionsList);

        // WHEN
        List<ClosestAttraction> result = userService
                .getTheFiveClosestAttractions("UNKNOW");

        // THEN
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @Tag("getTheFiveClosestAttractions")
    @DisplayName("Get The Five Closest Attractions - Error - Empty list attractions")
    public void givenNoAttractions_whenGetTheFiveClosestAttraction_thenReturnEmptyList() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Location tourEiffelLocation = new Location(48.858331, 2.294481);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                tourEiffelLocation, new Date()));

        attractionsList.clear();
        when(microserviceGpsProxy.getClosestAttractions(user.getUserName()))
                .thenReturn(attractionsList);

        // WHEN
        List<ClosestAttraction> result = userService
                .getTheFiveClosestAttractions(user.getUserName());

        // THEN
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @Tag("trackUserLocation")
    @DisplayName("Track User Location - OK")
    public void givenValidUser_whenTrackUserLocation_thenReturnVisitedLocation()
            throws InterruptedException {
        // GIVEN
        User user = new User(UUID.randomUUID(), "username", "029988776655",
                "email@gmail.fr");
        userService.addUser(user);

        VisitedLocationDto visitedLocationDto = new VisitedLocationDto(
                user.getUserId(), 48.858331, 2.294481, new Date());
        when(microserviceGpsProxy.getUserInstantLocation(user.getUserId()))
                .thenReturn(visitedLocationDto);

        assertThat(user.getVisitedLocations().size()).isEqualTo(0);

        // WHEN
        userService.trackUserLocation(user);
        Thread.sleep(100);

        // THEN
        assertThat(user.getVisitedLocations().size()).isEqualTo(1);
    }

}
