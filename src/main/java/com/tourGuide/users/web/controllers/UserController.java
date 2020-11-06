package com.tourGuide.users.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.services.IUserService;
import com.tourGuide.users.web.exceptions.ApiRequestException;

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

}
