package com.tourGuide.users.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tripPricer.Provider;

@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Getter
    @Setter
    private UUID userId;

    @Getter
    private String userName;

    @Getter
    @Setter
    private String phoneNumber;

    @Getter
    @Setter
    private String emailAddress;

    @Getter
    @Setter
    private Date latestLocationTimestamp;

    @Getter
    @Setter
    private List<Provider> tripDeals = new ArrayList<>();

    private List<VisitedLocation> visitedLocations = new ArrayList<>();

    private List<UserReward> userRewards = new ArrayList<>();

    private UserPreferences userPreferences = new UserPreferences();

    public User(UUID userId, String userName, String phoneNumber,
            String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public User(String userName, String phoneNumber, String emailAddress) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
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

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

}
