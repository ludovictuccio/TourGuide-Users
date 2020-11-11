package com.tourGuide.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.repository.InternalUserRepository;
import com.tourGuide.users.tracker.Tracker;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import tripPricer.TripPricer;

@Service
public class UserService implements IUserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private final GpsUtil gpsUtil;
//    private final RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;
    boolean testMode = true;

    private InternalUserRepository internalUserDao;

    public UserService(GpsUtil gpsUtil
    // , RewardsService rewardsService
    ) {
        this.gpsUtil = gpsUtil;
        // this.rewardsService = rewardsService;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            internalUserDao = new InternalUserRepository();
            internalUserDao.initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**
     * Method service used to retrieve the last user visited location.
     *
     * @param user
     * @return visitedLocation
     */
    public VisitedLocation getUserLocation(final User user) {
        VisitedLocation visitedLocation = (user.getVisitedLocations()
                .size() > 0) ? user.getLastVisitedLocation()
                        : trackUserLocation(user);
        return visitedLocation;
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
     * Method service used when no location already exist for an user. This
     * method will search localisation and add it in user visited locations.
     *
     * @param user
     * @return visitedLocation
     */
    public VisitedLocation trackUserLocation(final User user) {
        VisitedLocation visitedLocation = gpsUtil
                .getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        // rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    /**
     * Method service used to add a new user, if userName not already exists and
     * not empty.
     *
     * @param user
     */
    public boolean addUser(final User user) {
        boolean isAdded = false;
        if (!internalUserDao.internalUserMap.containsKey(user.getUserName())
                && !user.getUserName().isBlank()) {
            user.setUserId(UUID.randomUUID());
            internalUserDao.internalUserMap.put(user.getUserName(), user);
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
        return internalUserDao.internalUserMap.values().stream()
                .map(u -> u.getUserName()).collect(Collectors.toList());
    }

    /**
     * Method service used to get an user with his userName.
     *
     * @return user
     */
    public User getUser(final String userName) {
        return internalUserDao.internalUserMap.get(userName);
    }

    /**
     * Method service used for Tracker to retrieve all users.
     *
     * @return all users
     */
    public List<User> getAllUsers() {
        return internalUserDao.internalUserMap.values().stream()
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
        User user = this.internalUserDao.internalUserMap.get(userName);
        if (user == null) {
            isUpdated = false;
            return isUpdated;
        }
        user.setUserPreferences(userPreferences);
        return isUpdated;
    }

//    public List<Attraction> getNearByAttractions(
//            VisitedLocation visitedLocation) {
//        List<Attraction> nearbyAttractions = new ArrayList<>();
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
