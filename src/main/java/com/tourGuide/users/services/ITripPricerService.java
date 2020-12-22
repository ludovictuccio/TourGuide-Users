package com.tourGuide.users.services;

import java.util.List;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserReward;
import com.tourGuide.users.domain.dto.ProviderDto;

public interface ITripPricerService {

    /**
     * Method used to get offers from tripPricer.
     *
     * @param userName
     * @return ProviderDto list
     */
    List<ProviderDto> getTripDeals(final String userName);

    /**
     * Method used to get all user rewards list.
     *
     * @param user
     * @return user Rewards list
     */
    List<UserReward> getInstantUserRewards(final User user);

    /**
     * Method used to get all user's rewards points.
     *
     * @param user
     * @return an integer, all user's rewards points
     */
    int getAllUserRewardsPoints(final User user);

    /**
     * Used to simulate user with 4 user rewards for testing.
     */
    void testAddUserRewardsPoints();

}
