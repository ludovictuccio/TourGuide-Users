package com.tourGuide.users.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.tourGuide.users.domain.UserReward;
import com.tourGuide.users.domain.VisitedLocation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserRewardsDto {

    @Getter
    @Setter
    private UUID userId;

    private List<VisitedLocation> visitedLocations = new ArrayList<>();

    private List<UserReward> userRewards = new ArrayList<>();

    public UserRewardsDto(UUID userId, List<VisitedLocation> visitedLocations,
            List<UserReward> userRewards) {
        super();
        this.userId = userId;
        this.visitedLocations = visitedLocations;
        this.userRewards = userRewards;
    }

    public void addToVisitedLocations(VisitedLocation visitedLocation) {
        visitedLocations.add(visitedLocation);
    }

    public List<VisitedLocation> getVisitedLocations() {
        return visitedLocations;
    }

    public void clearVisitedLocations() {
        visitedLocations.clear();
    }

    public void addUserReward(UserReward userReward) {
        userRewards.add(userReward);
    }

    public List<UserReward> getUserRewards() {
        return userRewards;
    }

}