package com.tourGuide.users.services;

import java.util.List;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.UserDto;

public interface IUserService {

    boolean addUser(final User user);

    User getUser(String userName);

    UserDto getUserDto(String userName);

    List<String> getAllUsernames();

    List<User> getAllUsers();

    boolean updateUserPreferences(final String userName,
            final UserPreferences userPreferences);

    VisitedLocation getUserLocation(final User user);

    List<VisitedLocation> getAllUsersLocations();

    List<ClosestAttraction> getTheFiveClosestAttractions(String userName);

    VisitedLocation trackUserLocation(User user);

}
