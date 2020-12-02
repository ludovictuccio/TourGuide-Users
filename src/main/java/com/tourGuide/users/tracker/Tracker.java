package com.tourGuide.users.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    public Tracker(final UserService usersService) {
        this.userService = usersService;
        executorService.submit(this);
    }

    /**
     * Assures to shut down the Tracker thread
     */
    public void stopTracking() {
        isStopTracking = true;
        executorService.shutdownNow();
    }

    /**
     * Method used to track user location, and add it to user's visited location
     * history all the 5 minutes.
     */
    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();

        while (true) {
            if (Thread.currentThread().isInterrupted() || isStopTracking) {
                LOGGER.debug("Tracker stopping");
                break;
            }

            List<User> users = userService.getAllUsers();
            LOGGER.debug("Begin Tracker. Tracking " + users.size() + " users.");

            stopWatch.start();
            // users.forEach(u -> userService.trackUserLocation(u));

            try {
                userService.trackAllUsersLocation(users);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            stopWatch.stop();

            LOGGER.debug("Tracker Time Elapsed: "
                    + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
                    + " seconds.");
            stopWatch.reset();

            try {
                LOGGER.debug("Tracker sleeping");
                TimeUnit.SECONDS.sleep(TRACKING_POLLING_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Method used for test, with track all 2 seconds
     */
    public void runForTest() {
        StopWatch stopWatch = new StopWatch();

        List<User> users = new ArrayList<>();
        User user = new User(UUID.randomUUID(), "test", "0299887744",
                "email@gmail.com");
        users.add(user);

        stopWatch.start();
        users.forEach(u -> userService.trackUserLocation(u));
        stopWatch.stop();

        LOGGER.debug("Tracker Time Elapsed: "
                + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
                + " seconds.");
        stopWatch.reset();

        try {
            LOGGER.debug("Tracker sleeping");
            TimeUnit.SECONDS.sleep(TimeUnit.SECONDS.toSeconds(2));
        } catch (InterruptedException e) {
        }
    }

}
