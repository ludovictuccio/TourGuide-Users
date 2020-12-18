package com.tourGuide.users.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.UserDto;
import com.tourGuide.users.domain.dto.UserRewardsDto;

public interface IUserService {

    /**
     * Method service used to retrieve all the last users visited locations.
     *
     * @return a Map<String, Location> with: userId / user last location
     */
    Map<String, Location> getAllUsersLocations();

    /**
     * Method service used to add a new user, if userName not already exists and
     * not empty.
     *
     * @param user
     */
    boolean addUser(final User user);

    /**
     * Method service used to get an user with his userName.
     *
     * @param userName
     * @return user
     */
    User getUser(final String userName);

    /**
     * Method service used to get an user dto with his userName.
     *
     * @param userName
     * @return userDto
     */
    UserDto getUserDto(final String userName);

    /**
     * Method service used to get an userRewardsDto with his userName (to get
     * user UUId, userName, all VisitedLocations and rewards). Used with
     * tracking method.
     *
     * @param userId
     * @return userRewardsDto
     */
    UserRewardsDto getUserRewardsDto(final UUID userId);

    /**
     * Method service used for Tracker to retrieve all users.
     *
     * @return all users
     */
    List<User> getAllUsers();

    /**
     * Method service used to retrieve all users with existing VisitedLocation
     * in history.
     *
     * @return all users with existing VisitedLocation
     */
    List<User> getAllUsersWithVisitedLocations();

    /**
     * Method service used to update user preferences with userName.
     *
     * @return boolean isUpdated
     */
    boolean updateUserPreferences(final String userName,
            final UserPreferences userPreferences);

    /**
     * Method service used to retrieve the last user visited location.
     *
     * @param user
     * @return the last visited location or error 400
     */
    VisitedLocation getUserLocation(final User user);

    /**
     * This method call GPS microservice to return the five user's closest
     * attractions since his last location.
     *
     * @param userName
     * @return the five closest attractions
     */
    List<ClosestAttraction> getTheFiveClosestAttractions(final String userName);

    /**
     * Method used to track user's location, calling GPS microservice. The
     * location will be add to user's visited location history, and user's
     * rewards points will be updated.
     *
     * @param user
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CompletableFuture<?> trackUserLocation(final User user)
            throws InterruptedException, ExecutionException;

}
