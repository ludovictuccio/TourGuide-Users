package com.tourGuide.users.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.UserDto;
import com.tourGuide.users.domain.dto.UserRewardsDto;

public interface IUserService {

    boolean addUser(final User user);

    User getUser(final String userName);

    UserDto getUserDto(final String userName);

    UserRewardsDto getUserRewardsDto(final UUID userId);

    List<String> getAllUsernames();

    List<User> getAllUsers();

    List<User> getAllUsersWithVisitedLocations();

//    boolean updateUserPreferences(final String userName,
//            final UserPreferences userPreferences);

    VisitedLocation getUserLocation(final User user);

    Map<String, Location> getAllUsersLocations();

    List<ClosestAttraction> getTheFiveClosestAttractions(final String userName);

    CompletableFuture<?> trackUserLocation(final User user)
            throws InterruptedException, ExecutionException;

}
