package com.tourGuide.users.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.AttractionDto;
import com.tourGuide.users.helper.InternalTestHelper;
import com.tourGuide.users.proxies.MicroserviceGpsProxy;
import com.tourGuide.users.proxies.MicroserviceRewardsProxy;
import com.tourGuide.users.repository.InternalUserRepository;
import com.tourGuide.users.web.exceptions.InvalidLocationException;

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

    private User user;
    private User user2;

    private List<AttractionDto> attractionsList;

    private AttractionDto tourEiffel;
    private AttractionDto louvre;
    private AttractionDto lesInvalides;
    private AttractionDto lePantheon;
    private AttractionDto disneylandParis;
    private AttractionDto futuroscope;
    private AttractionDto vieuxPortDeMarseille;
    private AttractionDto basiliqueNotreDameDeFourviere;

    @BeforeEach
    public void setUpPerTest() {
        internalUserRepository.internalUserMap.clear();
        InternalTestHelper.setInternalUserNumber(0);

        tourEiffel = new AttractionDto("Tour Eiffel",
                new Location(48.858331, 2.294481), "Paris", "France",
                UUID.randomUUID());
        louvre = new AttractionDto("Musée du Louvre",
                new Location(48.861147, 2.338028), "Paris", "France",
                UUID.randomUUID());
        lesInvalides = new AttractionDto("Hôtel des Invalides",
                new Location(48.853241, 2.312107), "Paris", "France",
                UUID.randomUUID());
        lePantheon = new AttractionDto("Le Panthéon",
                new Location(48.846012, 2.345924), "Paris", "France",
                UUID.randomUUID());
        disneylandParis = new AttractionDto("Disneyland Paris",
                new Location(48.872448, 2.776794), "Paris", "France",
                UUID.randomUUID());
        futuroscope = new AttractionDto("Futuroscope",
                new Location(46.667134, 0.367085), "Poitiers", "France",
                UUID.randomUUID());
        vieuxPortDeMarseille = new AttractionDto("Vieux-Port de Marseille",
                new Location(43.295364, 5.37439), "Marseille", "France",
                UUID.randomUUID());
        basiliqueNotreDameDeFourviere = new AttractionDto(
                "Basilique Notre-Dame de Fourvière",
                new Location(45.761347, 4.821883), "Lyon", "France",
                UUID.randomUUID());

        attractionsList = new ArrayList<>();
        attractionsList.add(tourEiffel);
        attractionsList.add(louvre);
        attractionsList.add(lesInvalides);
        attractionsList.add(lePantheon);
        attractionsList.add(disneylandParis);
        attractionsList.add(futuroscope);
        attractionsList.add(vieuxPortDeMarseille);
        attractionsList.add(basiliqueNotreDameDeFourviere);
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

        when(microserviceGpsProxy.getAllAttractions())
                .thenReturn(attractionsList);

        when(microserviceRewardsProxy.getAttractionRewards(
                tourEiffel.getAttractionId(), user.getUserId())).thenReturn(10);
        when(microserviceRewardsProxy.getAttractionRewards(
                lesInvalides.getAttractionId(), user.getUserId()))
                        .thenReturn(20);
        when(microserviceRewardsProxy.getAttractionRewards(
                louvre.getAttractionId(), user.getUserId())).thenReturn(30);
        when(microserviceRewardsProxy.getAttractionRewards(
                lePantheon.getAttractionId(), user.getUserId())).thenReturn(40);
        when(microserviceRewardsProxy.getAttractionRewards(
                disneylandParis.getAttractionId(), user.getUserId()))
                        .thenReturn(50);

        // WHEN
        List<ClosestAttraction> result = userService
                .getTheFiveClosestAttractions(user.getUserName(),
                        user.getVisitedLocations()
                                .get(user.getVisitedLocations().size() - 1));

        // THEN
        assertThat(result.size()).isEqualTo(5);

        assertThat(result.get(0).getAttractionName()
                .contains(tourEiffel.getAttractionName())).isTrue();
        assertThat(result.get(1).getAttractionName()
                .contains(lesInvalides.getAttractionName())).isTrue();
        assertThat(result.get(2).getAttractionName()
                .contains(louvre.getAttractionName())).isTrue();
        assertThat(result.get(3).getAttractionName()
                .contains(lePantheon.getAttractionName())).isTrue();
        assertThat(result.get(4).getAttractionName()
                .contains(disneylandParis.getAttractionName())).isTrue();

        assertThat(result.get(0).getAttractionRewardsPoints()).isEqualTo(10);
        assertThat(result.get(1).getAttractionRewardsPoints()).isEqualTo(20);
        assertThat(result.get(2).getAttractionRewardsPoints()).isEqualTo(30);
        assertThat(result.get(3).getAttractionRewardsPoints()).isEqualTo(40);
        assertThat(result.get(4).getAttractionRewardsPoints()).isEqualTo(50);
    }

    @Test
    @Tag("getTheFiveClosestAttractions")
    @DisplayName("Get The Five Closest Attractions - Ok - User in Marseille city")
    public void givenUserInMarseillleCityLocation_whenGetTheFiveClosestAttraction_thenReturnValidAttractions() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Location marseilleLocation = new Location(43.291928, 5.378692);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                marseilleLocation, new Date()));
        userService.addUser(user);

        when(microserviceGpsProxy.getAllAttractions())
                .thenReturn(attractionsList);

        when(microserviceRewardsProxy.getAttractionRewards(
                vieuxPortDeMarseille.getAttractionId(), user.getUserId()))
                        .thenReturn(10);
        when(microserviceRewardsProxy.getAttractionRewards(
                basiliqueNotreDameDeFourviere.getAttractionId(),
                user.getUserId())).thenReturn(20);
        when(microserviceRewardsProxy.getAttractionRewards(
                futuroscope.getAttractionId(), user.getUserId()))
                        .thenReturn(30);
        when(microserviceRewardsProxy.getAttractionRewards(
                disneylandParis.getAttractionId(), user.getUserId()))
                        .thenReturn(40);
        when(microserviceRewardsProxy.getAttractionRewards(
                lePantheon.getAttractionId(), user.getUserId())).thenReturn(50);
        // WHEN
        List<ClosestAttraction> result = userService
                .getTheFiveClosestAttractions(user.getUserName(),
                        user.getVisitedLocations()
                                .get(user.getVisitedLocations().size() - 1));

        // THEN
        assertThat(result.size()).isEqualTo(5);

        assertThat(result.get(0).getAttractionName()
                .contains(vieuxPortDeMarseille.getAttractionName())).isTrue();
        assertThat(result.get(1).getAttractionName()
                .contains(basiliqueNotreDameDeFourviere.getAttractionName()))
                        .isTrue();
        assertThat(result.get(2).getAttractionName()
                .contains(futuroscope.getAttractionName())).isTrue();
        assertThat(result.get(3).getAttractionName()
                .contains(disneylandParis.getAttractionName())).isTrue();
        assertThat(result.get(4).getAttractionName()
                .contains(lePantheon.getAttractionName())).isTrue();

        assertThat(result.get(0).getAttractionRewardsPoints()).isEqualTo(10);
        assertThat(result.get(1).getAttractionRewardsPoints()).isEqualTo(20);
        assertThat(result.get(2).getAttractionRewardsPoints()).isEqualTo(30);
        assertThat(result.get(3).getAttractionRewardsPoints()).isEqualTo(40);
        assertThat(result.get(4).getAttractionRewardsPoints()).isEqualTo(50);
    }

    @Test
    @Tag("getTheFiveClosestAttractions")
    @DisplayName("Get The Five Closest Attractions - Error - No visited location")
    public void givenUserwithoutVisitedLocation_whenGetTheFiveClosestAttraction_thenReturnEmptyList() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        when(microserviceGpsProxy.getAllAttractions())
                .thenReturn(attractionsList);

        // WHEN
        List<ClosestAttraction> result = userService
                .getTheFiveClosestAttractions(user.getUserName(), null);

        // THEN
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @Tag("getTheFiveClosestAttractions")
    @DisplayName("Get The Five Closest Attractions - Error - Null attractions")
    public void givenNoAttractions_whenGetTheFiveClosestAttraction_thenReturnEmptyList() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Location tourEiffelLocation = new Location(48.858331, 2.294481);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                tourEiffelLocation, new Date()));
        when(microserviceGpsProxy.getAllAttractions()).thenReturn(null);

        // WHEN
        List<ClosestAttraction> result = userService
                .getTheFiveClosestAttractions(user.getUserName(),
                        user.getVisitedLocations()
                                .get(user.getVisitedLocations().size() - 1));

        // THEN
        assertThat(result.size()).isEqualTo(0);
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
    @Tag("addUser_getAllUsernames")
    @DisplayName("getAllUsernames")
    public void givenTwoUser_whenGetAllUsernames_thenReturnTwo() {
        // GIVEN
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        List<String> allUsers = userService.getAllUsernames();

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
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        boolean result = userService.addUser(new User(UUID.randomUUID(),
                "newUser", "123456789", "email@email.com"));

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
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        boolean result = userService.addUser(user);

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
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000",
                "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        // WHEN
        boolean result = userService.addUser(new User(UUID.randomUUID(), "",
                "123456789", "email@email.com"));

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
        List<VisitedLocation> result = userService.getAllUsersLocations();

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

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
