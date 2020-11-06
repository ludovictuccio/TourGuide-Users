package com.tourGuide.users.services;

import com.tourGuide.users.domain.User;

import gpsUtil.location.VisitedLocation;

public interface IUserService {

    VisitedLocation getUserLocation(final User user);

    User getUser(String userName);
}
