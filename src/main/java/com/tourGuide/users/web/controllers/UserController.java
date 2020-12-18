package com.tourGuide.users.web.controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
import com.tourGuide.users.services.ITripPricerService;
import com.tourGuide.users.services.IUserService;
import com.tourGuide.users.web.exceptions.InvalidLocationException;
import com.tourGuide.users.web.exceptions.UserInputException;

import io.swagger.annotations.ApiOperation;

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
     * Controller method used to return the last saved user location.
     *
     * @param userName
     * @return user location or error 400
     */
    @ApiOperation(value = "GET location", notes = "Need param userName - Return 200 OK or 400 bad request. Method used to return the last saved user location", response = Location.class)
    @GetMapping("/getLocation")
    public Location getLocation(@RequestParam final String userName) {
        User user = retrieveUser(userName);
        checkExistingUser(userName);
        VisitedLocation visitedLocation = userService.getUserLocation(user);
        return visitedLocation.location;
    }

    /**
     * Controller method used to return all the last users locations saved (the
     * last visited location saved in the history).
     *
     * @return all users locations
     */
    @ApiOperation(value = "GET all locations", notes = "Return 200 OK - Method used to return all the last users locations saved (the last visited location saved in the history)")
    @GetMapping("/getAllUsersLocations")
    public Map<String, Location> getAllUsersLocations() {
        return userService.getAllUsersLocations();
    }

    /**
     * Controller method used to add a new user.
     *
     * @param user
     * @return String or error 400
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @ApiOperation(value = "POST add new User", notes = "Need body user - Location will be tracked - Return 200 OK or 400 bad request.", response = String.class)
    @PostMapping
    public String addUser(@RequestBody final User user)
            throws InterruptedException, ExecutionException {
        boolean isAdded = userService.addUser(user);
        if (!isAdded) {
            throw new UserInputException("NOT ADDED. User with userName: "
                    + user.getUserName().toUpperCase() + " already exists");
        }
        userService.trackUserLocation(user);
        return "User added";
    }

    /**
     * Controller method used to get an user with his userName.
     *
     * @param userName
     * @return user
     */
    @ApiOperation(value = "GET User", notes = "Need param userName - Return 200 OK or 400 bad request.", response = User.class)
    @GetMapping("/getUser")
    public User getUser(String userName) {
        User user = retrieveUser(userName);
        checkExistingUser(userName);
        return user;
    }

    /**
     * Controller method used to return a user dto with user UUID & the last
     * location. Method used with gps microservice.
     *
     * @param userName
     * @return userDto
     */
    @ApiOperation(value = "GET UserDto", notes = "Need PathVariable userName - Method used to return a user dto with user UUID & the last location, used with gps microservice.", response = UserDto.class)
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
     * Method controller used to update user preferences with userName.
     * 
     * @param userName
     * @param userPreferences
     * @return String
     */
    @ApiOperation(value = "PUT Update user preferences", notes = "Need param userName & body UserPreferences - Return 200 OK or 400 bad request - Method used to update user preferences with userName.", response = String.class)
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
    @ApiOperation(value = "GET the five closest attractions", notes = "Need param userName - Return 200 OK or 400 bad request - Method used to get the five user's closest attractions.", response = ClosestAttraction.class)
    @GetMapping("/getTheFiveClosestAttractions")
    public List<ClosestAttraction> getTheFiveClosestAttractions(
            @RequestParam String userName) {
        User user = retrieveUser(userName);
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
    @ApiOperation(value = "GET Trip Deals", notes = "Need param userName - Return 200 OK or 400 bad request - Method used to get user's trip deals.", response = ProviderDto.class)
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
    @ApiOperation(value = "GET user rewards", notes = "Need param userName - Return 200 OK or 400 bad request - Method used to get user's rewards.", response = ProviderDto.class)
    @GetMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam final String userName) {
        checkExistingUser(userName);
        tripPricerService.getInstantUserRewards(getUser(userName));
        return getUser(userName).getUserRewards();
    }

    /**
     * Method controller used to get user's rewards points
     *
     * @param userName
     * @return integer user's rewards points
     */
    @ApiOperation(value = "GET All user rewards points", notes = "Need param userName - Return 200 OK or 400 bad request - Method used to get user's rewards points.", response = Integer.class)
    @GetMapping("/getAllUserRewardsPoints")
    public int getAllUserRewardsPoints(@RequestParam final String userName) {
        checkExistingUser(userName);
        return tripPricerService.getAllUserRewardsPoints(getUser(userName));
    }

    private User retrieveUser(final String userName) {
        return userService.getUser(userName);
    }

    public void checkExistingUser(final String userName) {
        User user = retrieveUser(userName);
        if (user == null) {
            throw new UserInputException(
                    "User not found with userName: " + userName);
        }
    }

    @ApiOperation(value = "GET TEST add users rewards", notes = "Add visitedlocations for internalUser1 to add 4 rewards")
    @GetMapping("/testAddUserRewardsPoints")
    public void testAddUserRewardsPoints() {
        tripPricerService.testAddUserRewardsPoints();
    }

}
