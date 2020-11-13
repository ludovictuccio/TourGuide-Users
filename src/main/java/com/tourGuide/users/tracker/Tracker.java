package com.tourGuide.users.tracker;

//public class Tracker extends Thread {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(Tracker.class);
//
//    private static final long trackingPollingInterval = TimeUnit.MINUTES
//            .toSeconds(5);
//
//    private final ExecutorService executorService = Executors
//            .newSingleThreadExecutor();
//
//    private final UserService userService;
//
//    private boolean stop = false;
//
//    public Tracker(final UserService usersService) {
//        this.userService = usersService;
//        executorService.submit(this);
//    }
//
//    /**
//     * Assures to shut down the Tracker thread
//     */
//    public void stopTracking() {
//        stop = true;
//        executorService.shutdownNow();
//    }
//
//    @Override
//    public void run() {
//        StopWatch stopWatch = new StopWatch();
//        while (true) {
//            if (Thread.currentThread().isInterrupted() || stop) {
//                LOGGER.debug("Tracker stopping");
//                break;
//            }
//
//            List<User> users = userService.getAllUsers();
//            LOGGER.debug("Begin Tracker. Tracking " + users.size() + " users.");
//            stopWatch.start();
//            users.forEach(u -> new Location(
//                    ThreadLocalRandom.current().nextDouble(-85.05112878D,
//                            85.05112878D),
//                    ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D))
//            // ADD RANDOM BEFORE USE GPS MICROSERVICE
//            // userService.trackUserLocation(u)
//            );
//            stopWatch.stop();
//            LOGGER.debug("Tracker Time Elapsed: "
//                    + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
//                    + " seconds.");
//            stopWatch.reset();
//            try {
//                LOGGER.debug("Tracker sleeping");
//                TimeUnit.SECONDS.sleep(trackingPollingInterval);
//            } catch (InterruptedException e) {
//                break;
//            }
//        }
//
//    }
//}
