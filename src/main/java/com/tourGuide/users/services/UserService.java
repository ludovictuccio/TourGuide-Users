package com.tourGuide.users.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.UserDto;
import com.tourGuide.users.domain.dto.UserRewardsDto;
import com.tourGuide.users.proxies.MicroserviceGpsProxy;
import com.tourGuide.users.proxies.MicroserviceRewardsProxy;
import com.tourGuide.users.repository.InternalUserRepository;
import com.tourGuide.users.tracker.Tracker;
import com.tourGuide.users.web.exceptions.InvalidLocationException;

@Service
public class UserService implements IUserService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(UserService.class);

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private MicroserviceGpsProxy microserviceGpsProxy;

    @Autowired
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @Autowired
    public Tracker tracker;

    private ExecutorService executorService = Executors
            .newFixedThreadPool(1000);

    /**
     * Used to initialize users tests to generate. Set to false if no test mode.
     */
    public boolean isTestMode = true;

    /**
     * Used to stop tracking while performance IT. Set to true if performance IT
     * mode.
     */
    public boolean isPerformanceTestMode = false;

    /**
     * @param userRepo
     * @param gpsProxy
     * @param rewardsProxy
     */
    public UserService(InternalUserRepository userRepo,
            MicroserviceGpsProxy gpsProxy,
            MicroserviceRewardsProxy rewardsProxy) {
        this.internalUserRepository = userRepo;
        this.microserviceGpsProxy = gpsProxy;
        this.microserviceRewardsProxy = rewardsProxy;

        if (isPerformanceTestMode) {
            LOGGER.debug("Performance IT mode ON.");
            internalUserRepository.initializeInternalUsers();
        } else if (isTestMode) {
            LOGGER.debug("Test mode ON.");
            internalUserRepository.initializeInternalUsers();
            this.tracker = new Tracker(this);
            tracker.startTracking();
        } else {
            LOGGER.debug("Tests mode OFF.");
            this.tracker = new Tracker(this);
            tracker.startTracking();
        }
    }

    /**
     * Method used to get the last user's VisitedLocation.
     *
     * @param user
     * @return the last user's VisitedLocation
     */
    public VisitedLocation getLastVisitedLocation(final User user) {
        return user.getVisitedLocations()
                .get(user.getVisitedLocations().size() - 1);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public Map<String, Location> getAllUsersLocations() {

        // create a map with: userId / user last location
        Map<String, Location> allUsersLocation = new HashMap<>();

        getAllUsersWithVisitedLocations()
                .forEach(u -> allUsersLocation.put(u.getUserId().toString(),
                        getLastVisitedLocation(u).location));
        return allUsersLocation;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public User getUser(final String userName) {
        return internalUserRepository.internalUserMap.get(userName);
    }

    /**
     * {@inheritDoc}
     */
    public UserDto getUserDto(final String userName)
            throws NullPointerException {
        User user = getUser(userName);
        UserDto userDto = new UserDto(user.getUserId(),
                getLastVisitedLocation(user).getLocation());
        return userDto;
    }

    /**
     * Method to retrieve user by his UUID.
     *
     * @param userId
     * @return user
     */
    public Optional<User> getUserByUuid(final UUID userId)
            throws NullPointerException {
        Optional<User> user = Optional.of(internalUserRepository.internalUserMap
                .values().stream().filter(u -> u.getUserId().equals(userId))
                .findAny().orElse(null));
        return user;
    }

    /**
     * {@inheritDoc}
     */
    public UserRewardsDto getUserRewardsDto(final UUID userId)
            throws NullPointerException {
        Optional<User> user = getUserByUuid(userId);
        if (user.equals(null)) {
            return null;
        }
        UserRewardsDto userRewardsDto = new UserRewardsDto(
                user.get().getUserId(), user.get().getVisitedLocations(),
                user.get().getUserRewards());
        return userRewardsDto;
    }

    /**
     * {@inheritDoc}
     */
    public List<User> getAllUsers() {
        return internalUserRepository.internalUserMap.values().stream()
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public List<ClosestAttraction> getTheFiveClosestAttractions(
            final String userName) {
        return microserviceGpsProxy.getClosestAttractions(userName);
    }

    /**
     * {@inheritDoc}
     */
    @Async
    public CompletableFuture<?> trackUserLocation(final User user) {
        return CompletableFuture.supplyAsync(() -> {
            return microserviceGpsProxy
                    .getUserInstantLocation(user.getUserId());
        }, executorService).thenAccept(visitedLocationDto -> {
            user.addToVisitedLocations(
                    new VisitedLocation(visitedLocationDto.getUserId(),
                            new Location(visitedLocationDto.getLatitude(),
                                    visitedLocationDto.getLongitude()),
                            visitedLocationDto.getTimeVisited()));
        }).thenRunAsync(() -> trackUserRewards(user));
    }

    /**
     * Method used to update user rewards.
     *
     * @param user
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Async
    public CompletableFuture<?> trackUserRewards(final User user) {
        user.getUserRewards().clear();

        return CompletableFuture.supplyAsync(() -> {
            return microserviceRewardsProxy
                    .calculateRewards(getUserRewardsDto(user.getUserId()));
        }, executorService).thenAccept(u -> {
            u.stream().forEach(reward -> user.addUserReward(reward));
        });
    }

}
