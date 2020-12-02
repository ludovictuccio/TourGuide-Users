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
import com.tourGuide.users.domain.dto.UserRewardsDto;
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
     * Method used to get offers from tripPricer.
     *
     * @param userName
     * @return ProviderDto list
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
     * Method used to get all user's rewards points.
     *
     * @param user
     * @return an integer, all user's rewards points
     */
    public int getAllUserRewardsPoints(final User user) {
        getUserRewards(user);

        return user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints())
                .sum();
    }

    /**
     * Method used to get all user rewards list.
     *
     * @param user
     * @return user Rewards list
     */
    public List<UserReward> getUserRewards(final User user) {

        UserRewardsDto userRewardsDto = microserviceRewardsProxy
                .calculateRewards(user.getUserName());

        user.getUserRewards().clear();

        for (UserReward rewards : userRewardsDto.getUserRewards()) {
            user.addUserReward(rewards);
        }
        return user.getUserRewards();
    }

    public void testAddUserRewardsPoints() {
        // add to internalUser1 visited locations for existing attraction from
        // gpsUtil
        User user = userService.getUser("internalUser1");

        Location existingAttraction1 = new Location(33.817595, -117.922008);
        Location existingAttraction2 = new Location(43.582767, -110.821999);
        Location existingAttraction3 = new Location(35.141689, -115.510399);
        Location existingAttraction4 = new Location(33.881866, -115.90065);

        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction1, new Date()));
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction1, new Date()));
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction2, new Date()));
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction4, new Date()));
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction3, new Date()));
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                existingAttraction4, new Date()));
        // added 4 user rewards
    }

}
