package com.tourGuide.users.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.services.UserService;

@Component
public class Tracker extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tracker.class);

    private final UserService userService;

    /**
     * Used to update user's history visited location, all the 5 minutes.
     */
    private static final long TRACKING_POLLING_INTERVAL = TimeUnit.MINUTES
            .toSeconds(5);

    private final ExecutorService executorService = Executors
            .newSingleThreadExecutor();

    private boolean isStopTracking = false;

    public Tracker(UserService userService) {
        this.userService = userService;
    }

    public void startTracking() {
        isStopTracking = false;
        executorService.submit(this);
    }

    /**
     * Method used to track user location, add it to user's visited location
     * history, and update user Rewards all the 5 minutes.
     */
    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        while (true) {
            if (Thread.currentThread().isInterrupted() || isStopTracking) {
                LOGGER.debug("Tracker stopping");
                break;
            } else {
                List<User> users = userService.getAllUsers();
                LOGGER.debug(
                        "Begin Tracker. Tracking " + users.size() + " users.");

                stopWatch.start();
                users.forEach(u -> {
                    userService.trackUserLocation(u);
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                stopWatch.stop();
                LOGGER.debug("TRACKING END for " + users.size()
                        + " users - Tracker Time Elapsed: "
                        + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
                        + " seconds.");
                stopWatch.reset();
            }
            try {
                LOGGER.debug("Tracker sleeping");
                TimeUnit.SECONDS.sleep(TRACKING_POLLING_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
