package com.tourGuide.users.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.UserDto;
import com.tourGuide.users.domain.dto.UserRewardsDto;
import com.tourGuide.users.domain.dto.VisitedLocationDto;
import com.tourGuide.users.proxies.MicroserviceGpsProxy;
import com.tourGuide.users.proxies.MicroserviceRewardsProxy;
import com.tourGuide.users.repository.InternalUserRepository;
import com.tourGuide.users.web.exceptions.InvalidLocationException;

@Service
public class UserService implements IUserService {

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private MicroserviceGpsProxy microserviceGpsProxy;

    @Autowired
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    /**
     * Used to initialize users tests. Set to false if no test mode.
     */
    boolean isTestMode = true;

    public UserService(InternalUserRepository userRepo,
            MicroserviceGpsProxy gpsProxy,
            MicroserviceRewardsProxy rewardsProxy) {
        super();
        this.internalUserRepository = userRepo;
        this.microserviceGpsProxy = gpsProxy;
        this.microserviceRewardsProxy = rewardsProxy;

        if (isTestMode) {
            internalUserRepository.initializeInternalUsers();
        }
    }

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
     * @return a Map<String, Location> with: userId / user last location
     */
    public Map<String, Location> getAllUsersLocations() {

        // create a map with: userId / user last location
        Map<String, Location> allUsersLocation = new HashMap<>();

        getAllUsersWithVisitedLocations()
                .forEach(u -> allUsersLocation.put(u.getUserId().toString(),
                        getLastVisitedLocation(u).location));

        return allUsersLocation;
    }

    public VisitedLocation getLastVisitedLocation(User user) {
        return user.getVisitedLocations()
                .get(user.getVisitedLocations().size() - 1);
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
     * @param userName
     * @return user
     */
    public User getUser(String userName) {
        return internalUserRepository.internalUserMap.get(userName);
    }

    /**
     * Method service used to get an user dto with his userName.
     *
     * @param userName
     * @return userDto
     */
    public UserDto getUserDto(final String userName) {
        User user = getUser(userName);
        UserDto userDto = new UserDto(user.getUserId(),
                user.getVisitedLocations()
                        .get(user.getVisitedLocations().size() - 1)
                        .getLocation());
        return userDto;
    }

    /**
     * Method service used to get an userRewardsDto with his userName (to get
     * user UUId, userName, all VisitedLocations and rewards).
     *
     * @param userName
     * @return userRewardsDto
     */
    public UserRewardsDto getUserRewardsDto(final String userName) {
        User user = getUser(userName);
        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                user.getUserName(), user.getVisitedLocations(),
                user.getUserRewards());
        return userRewardsDto;
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
     * Method service used to retrieve all users with existing VisitedLocation
     * in history.
     *
     * @return all users with existing VisitedLocation
     */
    public List<User> getAllUsersWithVisitedLocations() {
        List<User> usersWithExistingLocations = new ArrayList<>();

        for (User user : getAllUsers()) {
            if (!user.getVisitedLocations().isEmpty()) {
                usersWithExistingLocations.add(user);
            }
        }
        return usersWithExistingLocations;
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

        // Update user rewards
        microserviceRewardsProxy.calculateRewards(user.getUserName());

        return isUpdated;
    }

    /**
     * This method call GPS microservice to return the five user's closest
     * attractions.
     */
    public List<ClosestAttraction> getTheFiveClosestAttractions(
            final String userName) {
        return microserviceGpsProxy.getClosestAttractions(userName);
    }

    /**
     * Method used to track user's location, calling GPS microservice. The
     * location will be add to user's visited location history, and user's
     * rewards points will be updated.
     *
     * @param user
     * @return visitedLocation
     */
    public VisitedLocation trackUserLocation(final User user) {

        // call gps microservice to retrieve location
        VisitedLocationDto visitedLocationDto = microserviceGpsProxy
                .getUserInstantLocation(user.getUserName());

        // add location to user history
        VisitedLocation visitedLocation = new VisitedLocation(
                visitedLocationDto.getUserId(),
                new Location(visitedLocationDto.getLatitude(),
                        visitedLocationDto.getLongitude()),
                visitedLocationDto.getTimeVisited());
        user.addToVisitedLocations(visitedLocation);

        return visitedLocation;
    }

    public void trackAllUsersLocation(final List<User> allUsersList)
            throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        for (User user : allUsersList) {
            Runnable runnable = () -> {
                trackUserLocation(user);
            };
            executorService.execute(runnable);
        }
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.MINUTES);
        return;
    }

}
