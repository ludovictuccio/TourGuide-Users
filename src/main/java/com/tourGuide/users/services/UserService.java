package com.tourGuide.users.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.UserDto;
import com.tourGuide.users.domain.dto.VisitedLocationDto;
import com.tourGuide.users.proxies.MicroserviceGpsProxy;
import com.tourGuide.users.repository.InternalUserRepository;
import com.tourGuide.users.web.exceptions.InvalidLocationException;

@Service
public class UserService implements IUserService {

    // private final TripPricer tripPricer = new TripPricer();

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private MicroserviceGpsProxy microserviceGpsProxy;

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
    public User getUser(String userName) {
        return internalUserRepository.internalUserMap.get(userName);
    }

    public UserDto getUserDto(String userName) {
        User user = getUser(userName);
        UserDto userDto = new UserDto(user.getUserId(),
                user.getVisitedLocations()
                        .get(user.getVisitedLocations().size() - 1)
                        .getLocation());
        return userDto;
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
     * This method call GPS microservice to return the five user's closest
     * attractions.
     */
    public List<ClosestAttraction> getTheFiveClosestAttractions(
            String userName) {
        return microserviceGpsProxy.getClosestAttractions(userName);
    }

    /**
     * Method used to track user's location, calling GPS microservice.
     *
     * @param user
     * @return visitedLocation
     */
    public VisitedLocation trackUserLocation(User user) {

        VisitedLocationDto visitedLocationDto = microserviceGpsProxy
                .getUserInstantLocation(user.getUserName());

        VisitedLocation visitedLocation = new VisitedLocation(
                visitedLocationDto.getUserId(),
                new Location(visitedLocationDto.getLatitude(),
                        visitedLocationDto.getLongitude()),
                visitedLocationDto.getTimeVisited());

        user.addToVisitedLocations(visitedLocation);
        // rewardsService.calculateRewards(user);
        return visitedLocation;
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

}
