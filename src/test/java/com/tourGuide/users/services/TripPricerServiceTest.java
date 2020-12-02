package com.tourGuide.users.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.UserReward;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.AttractionDto;
import com.tourGuide.users.domain.dto.ProviderDto;
import com.tourGuide.users.domain.dto.UserRewardsDto;
import com.tourGuide.users.proxies.MicroserviceRewardsProxy;

import tripPricer.Provider;
import tripPricer.TripPricer;

@SpringBootTest
public class TripPricerServiceTest {

    @Autowired
    public ITripPricerService tripPricerService;

    @MockBean
    private UserService userService;

    @MockBean
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @MockBean
    private TripPricer tripPricer;

    private static final String TRIP_PRICER_API_KEY = "test-server-api-key";

    @Test
    @Tag("getTripDeals")
    @DisplayName("Get TripDeals - Ok")
    public void givenProviders_whenGet_thenReturnFiveTripDeals() {
        // GIVEN
        User user = new User(UUID.randomUUID(), "username", "029988776655",
                "email@gmail.fr");
        user.setUserPreferences(new UserPreferences(2, 1, 2, 1));

        List<VisitedLocation> allUsersVisitedLocations = new ArrayList<>();

        List<UserReward> allUsersRewards = new ArrayList<>();

        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                user.getUserName(), allUsersVisitedLocations, allUsersRewards);

        List<Provider> providers = new ArrayList<>();
        providers.add(new Provider(UUID.randomUUID(), "TripDeal 1", 0));
        providers.add(new Provider(UUID.randomUUID(), "TripDeal 2", 0));
        providers.add(new Provider(UUID.randomUUID(), "TripDeal 3", 0));
        providers.add(new Provider(UUID.randomUUID(), "TripDeal 4", 0));
        providers.add(new Provider(UUID.randomUUID(), "TripDeal 5", 0));

        when(tripPricer.getPrice(TRIP_PRICER_API_KEY, user.getUserId(),
                user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(), 0))
                        .thenReturn(providers);

        when(userService.getUser(user.getUserName())).thenReturn(user);

        when(microserviceRewardsProxy.calculateRewards(user.getUserName()))
                .thenReturn(userRewardsDto);

        // WHEN
        List<ProviderDto> result = tripPricerService
                .getTripDeals(user.getUserName());

        // THEN
        assertThat(result.size()).isEqualTo(5);
    }

    @Test
    @Tag("getAllUserRewardsPoints")
    @DisplayName("Get All UserRewards Points - Ok - 2 Rewards")
    public void givenUserWithTwoRewardsPoints_whenGetAll_thenReturnAdditionalValues() {
        // GIVEN
        User user = new User(UUID.randomUUID(), "username", "029988776655",
                "email@gmail.fr");

        Location location1 = new Location(33.817595, -117.922008);
        Location location2 = new Location(43.582767, -110.821999);

        AttractionDto attractionDto = new AttractionDto("Disneyland", location1,
                "Anaheim", "CA", UUID.randomUUID());

        List<VisitedLocation> allUsersVisitedLocations = new ArrayList<>();
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(),
                location1, new Date());
        VisitedLocation visitedLocation2 = new VisitedLocation(user.getUserId(),
                location2, new Date());
        allUsersVisitedLocations.add(visitedLocation);
        allUsersVisitedLocations.add(visitedLocation2);

        List<UserReward> allUsersRewards = new ArrayList<>();
        UserReward userReward = new UserReward(visitedLocation, attractionDto);
        UserReward userReward2 = new UserReward(visitedLocation, attractionDto);
        allUsersRewards.add(userReward);
        allUsersRewards.add(userReward2);

        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                user.getUserName(), allUsersVisitedLocations, allUsersRewards);

        userReward.setRewardPoints(100);
        user.addUserReward(userReward);
        userReward2.setRewardPoints(80);
        user.addUserReward(userReward2);

        when(microserviceRewardsProxy.calculateRewards(user.getUserName()))
                .thenReturn(userRewardsDto);

        // WHEN
        int result = tripPricerService.getAllUserRewardsPoints(user);

        // THEN
        assertThat(user.getUserRewards().size()).isEqualTo(2);
        assertThat(result).isEqualTo(180);
    }

    @Test
    @Tag("getAllUserRewardsPoints")
    @DisplayName("Get All UserRewards Points - Ok - 0 Rewards")
    public void givenUserWithoutRewards_whenGetAll_thenReturnZero() {
        // GIVEN
        User user = new User(UUID.randomUUID(), "username", "029988776655",
                "email@gmail.fr");

        List<VisitedLocation> allUsersVisitedLocations = new ArrayList<>();
        List<UserReward> allUsersRewards = new ArrayList<>();

        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                user.getUserName(), allUsersVisitedLocations, allUsersRewards);

        when(microserviceRewardsProxy.calculateRewards(user.getUserName()))
                .thenReturn(userRewardsDto);

        // WHEN
        int result = tripPricerService.getAllUserRewardsPoints(user);

        // THEN
        assertThat(user.getUserRewards().size()).isEqualTo(0);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @Tag("getUserRewards")
    @DisplayName("Get User Rewards - OK - 1 rewards")
    public void givenUserReward_whenGet_thenReturnCorrectValues() {
        // GIVEN
        User user = new User(UUID.randomUUID(), "username", "029988776655",
                "email@gmail.fr");

        Location location1 = new Location(33.817595, -117.922008);

        AttractionDto attractionDto = new AttractionDto("Disneyland", location1,
                "Anaheim", "CA", UUID.randomUUID());

        List<VisitedLocation> allUsersVisitedLocations = new ArrayList<>();
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(),
                location1, new Date());
        allUsersVisitedLocations.add(visitedLocation);

        List<UserReward> allUsersRewards = new ArrayList<>();
        UserReward userReward = new UserReward(visitedLocation, attractionDto);
        allUsersRewards.add(userReward);

        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                user.getUserName(), allUsersVisitedLocations, allUsersRewards);

        when(microserviceRewardsProxy.calculateRewards(user.getUserName()))
                .thenReturn(userRewardsDto);

        userReward.setRewardPoints(100);
        user.addUserReward(userReward);

        // WHEN
        List<UserReward> result = tripPricerService.getUserRewards(user);

        // THEN
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getRewardPoints()).isEqualTo(100);
    }

    @Test
    @Tag("getUserRewards")
    @DisplayName("Get User Rewards - OK - 0 rewards")
    public void givenNoUserReward_whenGet_thenReturnEmptyList() {
        // GIVEN
        User user = new User(UUID.randomUUID(), "username", "029988776655",
                "email@gmail.fr");
        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                user.getUserName(), new ArrayList<VisitedLocation>(),
                new ArrayList<UserReward>());
        when(microserviceRewardsProxy.calculateRewards(user.getUserName()))
                .thenReturn(userRewardsDto);

        // WHEN
        List<UserReward> result = tripPricerService.getUserRewards(user);

        // THEN
        assertThat(result.size()).isEqualTo(0);
    }
}
