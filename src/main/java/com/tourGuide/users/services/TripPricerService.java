package com.tourGuide.users.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserReward;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.ProviderDto;
import com.tourGuide.users.proxies.MicroserviceRewardsProxy;

import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TripPricerService implements ITripPricerService {

    @Autowired
    private UserService userService;

    @Autowired
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @Autowired
    private TripPricer tripPricer;

    private static final String TRIP_PRICER_API_KEY = "test-server-api-key";

    /**
     * {@inheritDoc}
     */
    public List<ProviderDto> getTripDeals(final String userName) {

        User user = userService.getUser(userName);

        int cumulatativeRewardPoints = getAllUserRewardsPoints(user);

        List<Provider> providers = tripPricer.getPrice(TRIP_PRICER_API_KEY,
                user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(),
                cumulatativeRewardPoints);
        user.setTripDeals(providers);

        List<ProviderDto> providerDtoList = new ArrayList<>();
        providers.forEach(p -> {
            providerDtoList.add(new ProviderDto(p.name, p.price, p.tripId));
        });
        return providerDtoList;
    }

    /**
     * {@inheritDoc}
     */
    public int getAllUserRewardsPoints(final User user) {
        getInstantUserRewards(user);
        return user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints())
                .sum();
    }

    /**
     * {@inheritDoc}
     */
    public List<UserReward> getInstantUserRewards(final User user) {
        List<UserReward> allUserRewards = microserviceRewardsProxy
                .calculateRewards(
                        userService.getUserRewardsDto(user.getUserId()));
        user.getUserRewards().clear();
        allUserRewards.stream().forEach(reward -> user.addUserReward(reward));
        return user.getUserRewards();
    }

    /**
     * {@inheritDoc}
     */
    public void testAddUserRewardsPoints() {
        // add to internalUser1 visited locations for existing attraction from
        // gpsUtil
        User user = userService.getUser("internalUser1");

        Location existingAttraction1 = new Location(33.817595, -117.922008);
        Location existingAttraction2 = new Location(43.582767, -110.821999);
        Location existingAttraction3 = new Location(35.141689, -115.510399);
        Location existingAttraction4 = new Location(33.881866, -115.90065);

        // Attraction 1
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction1, new Date()));
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction1, new Date()));

        // Attraction 2
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction2, new Date()));

        // Attraction 3
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction3, new Date()));

        // Attraction 4
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction4, new Date()));
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction4, new Date()));
    }

}
