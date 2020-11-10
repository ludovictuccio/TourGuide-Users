package com.tourGuide.users.services;

import java.util.List;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;

import gpsUtil.location.VisitedLocation;

public interface IUserService {

    VisitedLocation getUserLocation(final User user);

    User getUser(final String userName);

    boolean addUser(final User user);

    List<String> getAllUsernames();

    List<User> getAllUsers();

    boolean updateUserPreferences(final String userName,
            final UserPreferences userPreferences);

}
