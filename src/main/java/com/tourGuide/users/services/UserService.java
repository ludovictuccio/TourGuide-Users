package com.tourGuide.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.repository.InternalUserRepository;
import com.tourGuide.users.web.exceptions.InvalidLocationException;

@Service
public class UserService implements IUserService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(UserService.class);

    // private final TripPricer tripPricer = new TripPricer();

    @Autowired
    private InternalUserRepository internalUserRepository;

    /**
     * Method service used to retrieve the last user visited location.
     *
     * @param user
     * @return the last visited location or error 400
     */
    public VisitedLocation getUserLocation(final User user) {

        if (user.getVisitedLocations().size() == 0) {
            throw new InvalidLocationException(
                    "Not location available. Please wait less than 5 minutes.");
        }
        return user.getLastVisitedLocation();
    }

    /**
     * Method service used to retrieve all the last users visited locations.
     *
     * @param an user list
     * @return allUsersLocations, a VisitedLocation list
     */
    public List<VisitedLocation> getAllUsersLocations() {
        List<User> allUsers = getAllUsers();
        List<VisitedLocation> allUsersLocations = new ArrayList<>();

        for (User user : allUsers) {
            VisitedLocation visitedLocation = getUserLocation(user);
            allUsersLocations.add(visitedLocation);
        }
        return allUsersLocations;
    }

    /**
     * Method service used to add a new user, if userName not already exists and
     * not empty.
     *
     * @param user
     */
    public boolean addUser(final User user) {
        boolean isAdded = false;
        if (!internalUserRepository.internalUserMap
                .containsKey(user.getUserName())
                && !user.getUserName().isBlank()) {
            user.setUserId(UUID.randomUUID());
            internalUserRepository.internalUserMap.put(user.getUserName(),
                    user);
            isAdded = true;
        }
        return isAdded;
    }

    /**
     * Method service used to return all userNames list.
     *
     * @return all userNames list
     */
    public List<String> getAllUsernames() {
        return internalUserRepository.internalUserMap.values().stream()
                .map(u -> u.getUserName()).collect(Collectors.toList());
    }

    /**
     * Method service used to get an user with his userName.
     *
     * @return user
     */
    public User getUser(final String userName) {
        return internalUserRepository.internalUserMap.get(userName);
    }

    /**
     * Method service used for Tracker to retrieve all users.
     *
     * @return all users
     */
    public List<User> getAllUsers() {
        return internalUserRepository.internalUserMap.values().stream()
                .collect(Collectors.toList());
    }

    /**
     * Method service used to update user preferences with userName.
     *
     * @return boolean isUpdated
     */
    public boolean updateUserPreferences(final String userName,
            final UserPreferences userPreferences) {
        boolean isUpdated = true;
        User user = this.internalUserRepository.internalUserMap.get(userName);
        if (user == null) {
            isUpdated = false;
            return isUpdated;
        }
        user.setUserPreferences(userPreferences);
        return isUpdated;
    }

    // Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the
    // attractions.
    // The reward points for visiting each Attraction.
    // Note: Attraction reward points can be gathered from RewardsCentral
//    public List<Attraction> getTheFiveClosestAttractions(
//            VisitedLocation visitedLocation) {
//
//        List<Attraction> allAttractionsList = getAllAttractions();
//        List<Attraction> theFiveClosestAttractionsList = new ArrayList<>();
//
//        allAttractionsList.stream()
//                .sorted((attraction1, attraction2) -> Double.compare(
//                        rewardsService.getDistance(visitedLocation.location,
//                                attraction1),
//                        rewardsService.getDistance(visitedLocation.location,
//                                attraction2)))
//                .limit(5).forEach(attraction -> {
//                    theFiveClosestAttractionsList.add(attraction);
//                });
//        return theFiveClosestAttractionsList;
//    }
//
//    public List<Attraction> getNearByAttractions(
//            VisitedLocation visitedLocation) {
//        List<Attraction> nearbyAttractions = new ArrayList<>();
//
//        for (Attraction attraction : gpsUtil.getAttractions()) {
//            if (rewardsService.isWithinAttractionProximity(attraction,
//                    visitedLocation.location)) {
//                nearbyAttractions.add(attraction);
//            }
//        }
//
//        return nearbyAttractions;
//    }

//    public List<Provider> getTripDeals(User user) {
//        int cumulatativeRewardPoints = user.getUserRewards().stream()
//                .mapToInt(i -> i.getRewardPoints()).sum();
//        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey,
//                user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
//                user.getUserPreferences().getNumberOfChildren(),
//                user.getUserPreferences().getTripDuration(),
//                cumulatativeRewardPoints);
//        user.setTripDeals(providers);
//        return providers;
//    }

}
