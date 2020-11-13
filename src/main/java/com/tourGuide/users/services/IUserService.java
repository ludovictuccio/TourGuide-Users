package com.tourGuide.users.services;

import java.util.List;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;

public interface IUserService {

    boolean addUser(final User user);

    User getUser(final String userName);

    List<String> getAllUsernames();

    List<User> getAllUsers();

    boolean updateUserPreferences(final String userName,
            final UserPreferences userPreferences);

    VisitedLocation getUserLocation(final User user);

    List<VisitedLocation> getAllUsersLocations();

//    List<Attraction> getTheFiveClosestAttractions(
//            VisitedLocation visitedLocation);
//
//    List<Attraction> getAllAttractions();

}
