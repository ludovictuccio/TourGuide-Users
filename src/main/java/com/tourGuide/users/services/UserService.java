package com.tourGuide.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.AttractionDto;
import com.tourGuide.users.proxies.MicroserviceGpsProxy;
import com.tourGuide.users.proxies.MicroserviceRewardsProxy;
import com.tourGuide.users.repository.InternalUserRepository;
import com.tourGuide.users.util.DistanceCalculator;
import com.tourGuide.users.web.exceptions.InvalidLocationException;

@Service
public class UserService implements IUserService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(UserService.class);

    // private final TripPricer tripPricer = new TripPricer();

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private MicroserviceGpsProxy microserviceGpsProxy;

    @Autowired
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @Autowired
    private DistanceCalculator distanceCalculator;

    /**
     * Method service used to retrieve the last user visited location.
     *
     * @param user
     * @return the last visited location or error 400
     */
    public VisitedLocation getUserLocation(final User user) {

        List<VisitedLocation> allVisitedLocations = user.getVisitedLocations();

        if (allVisitedLocations.size() == 0) {
            throw new InvalidLocationException(
                    "Not location available. Please wait less than 5 minutes.");
        }
        return allVisitedLocations.get(allVisitedLocations.size() - 1);
    }

    /**
     * Method service used to retrieve all the last users visited locations.
     *
     * @param an user list
     * @return allUsersLocations, a VisitedLocation list
     */
    public List<VisitedLocation> getAllUsersLocations() {

        return internalUserRepository.internalUserMap.values().stream()
                .map(u -> u.getVisitedLocations()
                        .get(u.getVisitedLocations().size() - 1))
                .collect(Collectors.toList());
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

    /**
     * Method used to return the five closest attractions since his last visited
     * location.
     *
     * @param userName
     * @param visitedLocation the last user's visited location
     * @return a ClosestAttraction list, the five closest attractions
     */
    public List<ClosestAttraction> getTheFiveClosestAttractions(
            final String userName, final VisitedLocation visitedLocation) {

        User user = internalUserRepository.internalUserMap.get(userName);

        List<AttractionDto> attractionsList = microserviceGpsProxy
                .getAllAttractions();
        List<ClosestAttraction> theFiveClosestAttractions = new ArrayList<>();

        if (visitedLocation == null || attractionsList == null) {
            return theFiveClosestAttractions;
        }

        attractionsList.stream()
                .sorted((attraction1, attraction2) -> Double.compare(
                        distanceCalculator.getDistanceInMiles(
                                visitedLocation.location,
                                attraction1.getLocation()),
                        distanceCalculator.getDistanceInMiles(
                                visitedLocation.location,
                                attraction2.getLocation())))
                .limit(5).forEach(attraction -> {

                    int attractionRewardsPoints = microserviceRewardsProxy
                            .getAttractionRewards(attraction.getAttractionId(),
                                    user.getUserId());

                    ClosestAttraction closestAttraction = new ClosestAttraction(
                            attraction.getAttractionName(),
                            attraction.getLocation(),
                            visitedLocation.getLocation(),
                            distanceCalculator.getDistanceInMiles(
                                    attraction.getLocation(),
                                    visitedLocation.getLocation()),
                            attractionRewardsPoints);

                    theFiveClosestAttractions.add(closestAttraction);
                });

        return theFiveClosestAttractions;
    }

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
