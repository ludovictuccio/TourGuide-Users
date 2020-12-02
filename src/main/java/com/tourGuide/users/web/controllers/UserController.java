package com.tourGuide.users.web.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.domain.UserReward;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.ProviderDto;
import com.tourGuide.users.domain.dto.UserDto;
import com.tourGuide.users.domain.dto.UserRewardsDto;
import com.tourGuide.users.services.ITripPricerService;
import com.tourGuide.users.services.IUserService;
import com.tourGuide.users.web.exceptions.InvalidLocationException;
import com.tourGuide.users.web.exceptions.UserInputException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public IUserService userService;

    @Autowired
    public ITripPricerService tripPricerService;

    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * Controller method use to return the last saved user location.
     *
     * @param userName
     * @return user location or error 400
     */
    @GetMapping("/getLocation")
    public Location getLocation(@RequestParam final String userName) {
        User user = userService.getUser(userName);
        checkExistingUser(userName);
        VisitedLocation visitedLocation = userService.getUserLocation(user);
        return visitedLocation.location;
    }

    /**
     * Controller method use to return all the last users locations saved (the
     * last visited location saved in the history).
     *
     * @return all users locations
     */
    @GetMapping("/getAllUsersLocations")
    public Map<String, Location> getAllUsersLocations() {
        return userService.getAllUsersLocations();
    }

    /**
     * Controller method used to add a new user.
     *
     * @param user
     * @return String or error 400
     */
    @PostMapping
    public String addUser(@RequestBody final User user) {
        boolean isAdded = userService.addUser(user);
        if (!isAdded) {
            throw new UserInputException("NOT ADDED. User with userName: "
                    + user.getUserName().toUpperCase() + " already exists");
        }
        return "User added";
    }

    /**
     * Controller method used to get all usernames.
     *
     * @return all usernames
     */
    @GetMapping("/getAllUsernames")
    public List<String> getAllUsernames() {
        List<String> allUsers = userService.getAllUsernames();
        return allUsers;
    }

    /**
     * Controller method used to get an user with his userName.
     *
     * @param userName
     * @return user
     */
    @GetMapping("/getUser")
    public User getUser(String userName) {
        User user = userService.getUser(userName);
        checkExistingUser(userName);
        return user;
    }

    /**
     * Controller method used to return a user dto with user UUID & the last
     * location.
     *
     * @param userName
     * @return userDto
     */
    @GetMapping("/getUserDto/{userName}")
    public UserDto getUserDto(@PathVariable("userName") String userName) {
        UserDto userDto = userService.getUserDto(userName);
        if (userDto == null) {
            throw new UserInputException(
                    "User not found with userName: " + userName);
        }
        return userDto;
    }

    /**
     * Controller method used to return a user dto with user UUID & the last
     * location.
     *
     * @param userName
     * @return userDto
     */
    @GetMapping("/getUserRewardsDto/{userName}")
    public UserRewardsDto getUserRewardsDto(
            @PathVariable("userName") String userName) {
        UserRewardsDto userRewardsDto = userService.getUserRewardsDto(userName);
        if (userRewardsDto == null) {
            throw new UserInputException(
                    "User not found with userName: " + userName);
        }
        return userRewardsDto;
    }

    /**
     * Method controller used to update user preferences with userName.
     * 
     * @param userName
     * @param userPreferences
     * @return String
     */
    @PutMapping("/updatePreferences")
    public String updateUserPreferences(@RequestParam final String userName,
            @RequestBody final UserPreferences userPreferences) {

        boolean isUpdated = userService.updateUserPreferences(userName,
                userPreferences);

        if (!isUpdated) {
            throw new UserInputException(
                    "User not found with userName: " + userName);
        }
        return "User preferences updated for user: " + userName;
    }

    /**
     * Method controller used to get the five user's closest attractions.
     *
     * @param userName
     * @return the 5 user's closest attractions
     */
    @GetMapping("/getTheFiveClosestAttractions")
    public List<ClosestAttraction> getTheFiveClosestAttractions(
            @RequestParam String userName) {
        User user = userService.getUser(userName);
        checkExistingUser(userName);
        if (user.getVisitedLocations().size() == 0) {
            throw new InvalidLocationException(
                    "User without visited location. Please try in few minutes (less than 5 min).");
        }
        return userService.getTheFiveClosestAttractions(userName);
    }

    /**
     * Method controller used to get user's trip deals.
     *
     * @param userName
     * @return user's trip deals
     */
    @GetMapping("/getTripDeals")
    public List<ProviderDto> getTripDeals(@RequestParam final String userName) {
        checkExistingUser(userName);
        return tripPricerService.getTripDeals(userName);
    }

    /**
     * Method controller used to get user's rewards.
     *
     * @param userName
     * @return user's rewards
     */
    @GetMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam final String userName) {
        checkExistingUser(userName);
        return tripPricerService.getUserRewards(getUser(userName));
    }

    /**
     * Method controller used to get user's rewards points
     *
     * @param userName
     * @return integer user's rewards points
     */
    @GetMapping("/getAllUserRewardsPoints")
    public int getAllUserRewardsPoints(@RequestParam final String userName) {
        checkExistingUser(userName);
        return tripPricerService.getAllUserRewardsPoints(getUser(userName));
    }

    public void checkExistingUser(String userName) {
        User user = userService.getUser(userName);
        if (user == null) {
            throw new UserInputException(
                    "User not found with userName: " + userName);
        }
    }

    @GetMapping("/testAddUserRewardsPoints")
    public void testAddUserRewardsPoints() {
        tripPricerService.testAddUserRewardsPoints();
    }

}
