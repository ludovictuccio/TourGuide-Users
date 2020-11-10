package com.tourGuide.users.web.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserPreferences;
import com.tourGuide.users.services.IUserService;
import com.tourGuide.users.web.exceptions.ApiRequestException;
import com.tourGuide.users.web.exceptions.ApiRequestUsernameException;

import gpsUtil.location.VisitedLocation;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public IUserService userService;

    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * Controller method use to return the last saved user location.
     *
     * @param userName
     * @return user location or error 404
     */
    @GetMapping("/getLocation")
    public String getLocation(@RequestParam final String userName) {
        User user = userService.getUser(userName);
        if (user == null) {
            throw new ApiRequestException(
                    "User not found with userName: " + userName);
        } else {
            VisitedLocation visitedLocation = userService.getUserLocation(user);
            return JsonStream.serialize(visitedLocation.location);
        }
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
            throw new ApiRequestUsernameException(
                    "NOT ADDED. User with userName: "
                            + user.getUserName().toUpperCase()
                            + " already exists");
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
     * @return user
     */
    @GetMapping("/getUser")
    public User getUser(@RequestParam final String userName) {
        User user = userService.getUser(userName);
        if (user == null) {
            throw new ApiRequestException(
                    "User not found with userName: " + userName);
        }
        return user;
    }

    /**
     * Method controller used to update user preferences with userName.
     *
     * @return
     */
    @PutMapping("/updatePreferences")
    public String updateUserPreferences(@RequestParam final String userName,
            @RequestBody final UserPreferences userPreferences) {

        boolean isUpdated = userService.updateUserPreferences(userName,
                userPreferences);

        if (!isUpdated) {
            throw new ApiRequestException(
                    "User not found with userName: " + userName);
        }
        return "User preferences updated for user: " + userName;
    }

}
