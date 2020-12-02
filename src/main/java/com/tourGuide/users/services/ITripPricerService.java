package com.tourGuide.users.services;

import java.util.List;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserReward;
import com.tourGuide.users.domain.dto.ProviderDto;

public interface ITripPricerService {

    List<ProviderDto> getTripDeals(final String userName);

    List<UserReward> getUserRewards(final User user);

    int getAllUserRewardsPoints(final User user);

    void testAddUserRewardsPoints();

}
