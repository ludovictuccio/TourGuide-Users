package com.tourGuide.users.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.services.UserService;

public class Tracker extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tracker.class);

    private static final long trackingPollingInterval = TimeUnit.MINUTES
            .toSeconds(5);
    private final ExecutorService executorService = Executors
            .newSingleThreadExecutor();
    private final UserService userService;
    private boolean stop = false;

    public Tracker(final UserService usersService) {
        this.userService = usersService;
        executorService.submit(this);
    }

    /**
     * Assures to shut down the Tracker thread
     */
    public void stopTracking() {
        stop = true;
        executorService.shutdownNow();
    }

    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        while (true) {
            if (Thread.currentThread().isInterrupted() || stop) {
                LOGGER.debug("Tracker stopping");
                break;
            }

            List<User> users = userService.getAllUsers();
            LOGGER.debug("Begin Tracker. Tracking " + users.size() + " users.");
            stopWatch.start();
            users.forEach(u -> userService.trackUserLocation(u));
            stopWatch.stop();
            LOGGER.debug("Tracker Time Elapsed: "
                    + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
                    + " seconds.");
            stopWatch.reset();
            try {
                LOGGER.debug("Tracker sleeping");
                TimeUnit.SECONDS.sleep(trackingPollingInterval);
            } catch (InterruptedException e) {
                break;
            }
        }

    }
}
