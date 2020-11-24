package com.tourGuide.users;

import org.springframework.beans.factory.annotation.Autowired;

import com.tourGuide.users.services.UserService;

//@SpringBootTest
public class TestPerformance {

    @Autowired
    public UserService userService;

//    @Autowired
//    private GpsUtil gpsUtil;
//
//    static {
//        Locale.setDefault(Locale.US);
//    }
//    /*
//     * A note on performance improvements:
//     * 
//     * The number of users generated for the high volume tests can be easily
//     * adjusted via this method:
//     * 
//     * InternalTestHelper.setInternalUserNumber(100000);
//     * 
//     * 
//     * These tests can be modified to suit new solutions, just as long as the
//     * performance metrics at the end of the tests remains consistent.
//     * 
//     * These are performance metrics that we are trying to hit:
//     * 
//     * highVolumeTrackLocation: 100,000 users within 15 minutes:
//     * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
//     * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
//     *
//     * highVolumeGetRewards: 100,000 users within 20 minutes:
//     * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
//     * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
//     */
//
//    // @Ignore
//    @Test
//    public void highVolumeTrackLocation() {
//        gpsUtil = new GpsUtil();
////        RewardsService rewardsService = new RewardsService(gpsUtil,
////                new RewardCentral());
//        // Users should be incremented up to 100,000, and test finishes within
//        // 15 minutes
//        InternalTestHelper.setInternalUserNumber(1000);
//        userService = new UserService(gpsUtil
//        // , rewardsService
//        );
//
//        List<User> allUsers = new ArrayList<>();
//        allUsers = userService.getAllUsers();
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        for (User user : allUsers) {
//            userService.trackUserLocation(user);
//        }
//        stopWatch.stop();
//        userService.tracker.stopTracking();
//
//        System.out.println("highVolumeTrackLocation: Time Elapsed: "
//                + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
//                + " seconds.");
//        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS
//                .toSeconds(stopWatch.getTime()));
//    }

//    @Ignore
//    @Test
//    public void highVolumeGetRewards() {
//        GpsUtil gpsUtil = new GpsUtil();
////        RewardsService rewardsService = new RewardsService(gpsUtil,
////                new RewardCentral());
//
//        // Users should be incremented up to 100,000, and test finishes within
//        // 20 minutes
//        InternalTestHelper.setInternalUserNumber(100);
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        UserService tourGuideService = new UserService(gpsUtil, rewardsService);
//
//        Attraction attraction = gpsUtil.getAttractions().get(0);
//        List<User> allUsers = new ArrayList<>();
//        allUsers = tourGuideService.getAllUsers();
//        allUsers.forEach(u -> u.addToVisitedLocations(
//                new VisitedLocation(u.getUserId(), attraction, new Date())));
//
//        allUsers.forEach(u -> rewardsService.calculateRewards(u));
//
//        for (User user : allUsers) {
//            assertTrue(user.getUserRewards().size() > 0);
//        }
//        stopWatch.stop();
//        tourGuideService.tracker.stopTracking();
//
//        System.out.println("highVolumeGetRewards: Time Elapsed: "
//                + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
//                + " seconds.");
//        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS
//                .toSeconds(stopWatch.getTime()));
//    }

}
