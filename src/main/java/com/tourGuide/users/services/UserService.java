package com.tourGuide.users.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.helper.InternalTestHelper;
import com.tourGuide.users.tracker.Tracker;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
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

    public UserService(GpsUtil gpsUtil
    // , RewardsService rewardsService
    ) {
        this.gpsUtil = gpsUtil;
        // this.rewardsService = rewardsService;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
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
        if (!internalUserMap.containsKey(user.getUserName())
                && !user.getUserName().isBlank()) {
            user.setUserId(UUID.randomUUID());
            internalUserMap.put(user.getUserName(), user);
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
        return internalUserMap.values().stream().map(u -> u.getUserName())
                .collect(Collectors.toList());
    }

    /**
     * Method service used to get an user with his userName.
     *
     * @return user
     */
    public User getUser(final String userName) {
        return internalUserMap.get(userName);
    }

    /**
     * Method service used for Tracker to retrieve all users.
     *
     * @return all users
     */
    public List<User> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    /**
     * Method service used to update user preferences with userName.
     *
     * @return boolean isUpdated
     */
    public boolean updateUserPreferences(final String userName,
            final UserPreferences userPreferences) {
        boolean isUpdated = true;
        User user = internalUserMap.get(userName);
        if (user == null) {
            isUpdated = false;
            return isUpdated;
        }
        user.setUserPreferences(userPreferences);
        return isUpdated;
    }

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

//
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

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     * 
     * Methods Below: For Internal Testing
     * 
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing
    // purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();

    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber())
                .forEach(i -> {
                    String userName = "internalUser" + i;
                    String phone = "000";
                    String email = userName + "@tourGuide.com";
                    User user = new User(UUID.randomUUID(), userName, phone,
                            email);
                    generateUserLocationHistory(user);

                    internalUserMap.put(userName, user);
                });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber()
                + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(
                    new VisitedLocation(user.getUserId(),
                            new Location(generateRandomLatitude(),
                                    generateRandomLongitude()),
                            getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now()
                .minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}
