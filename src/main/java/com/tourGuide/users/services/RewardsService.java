package com.tourGuide.users.services;

//@Service
//public class RewardsService implements IRewardsService {
//
//    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
//
//    // proximity in miles
//    private int defaultProximityBuffer = 10;
//    private int proximityBuffer = defaultProximityBuffer;
//    private int attractionProximityRange = 200;
//
//    private final GpsUtil gpsUtil;
//    private final RewardCentral rewardsCentral;
//
//    public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
//        this.gpsUtil = gpsUtil;
//        this.rewardsCentral = rewardCentral;
//    }
//
//    public void setProximityBuffer(int proximityBuffer) {
//        this.proximityBuffer = proximityBuffer;
//    }
//
//    public void setDefaultProximityBuffer() {
//        proximityBuffer = defaultProximityBuffer;
//    }
//
//    public void calculateRewards(User user) {
//        List<VisitedLocation> userLocations = user.getVisitedLocations();
//        List<Attraction> attractions = gpsUtil.getAttractions();
//
//        for (VisitedLocation visitedLocation : userLocations) {
//            for (Attraction attraction : attractions) {
//                if (user.getUserRewards().stream()
//                        .filter(r -> r.attraction.attractionName
//                                .equals(attraction.attractionName))
//                        .count() == 0) {
//                    if (nearAttraction(visitedLocation, attraction)) {
//                        user.addUserReward(new UserReward(visitedLocation,
//                                attraction, getRewardPoints(attraction, user)));
//                    }
//                }
//            }
//        }
//    }
//
//    private int getRewardPoints(Attraction attraction, User user) {
//        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId,
//                user.getUserId());
//    }
//
////    public boolean isWithinAttractionProximity(Attraction attraction,
////            Location location) {
////        return getDistance(attraction, location) > attractionProximityRange
////                ? false
////                : true;
////    }
//
//    /**
//     * Method service used to return if an attraction is near than the user's
//     * visited location. Used to calculate user's rewards.
//     *
//     * @param visitedLocation
//     * @param attraction
//     * @return boolean true or false
//     */
//    boolean nearAttraction(VisitedLocation visitedLocation,
//            Attraction attraction) {
//        return getDistance(attraction,
//                visitedLocation.location) > proximityBuffer ? false : true;
//    }
//
//    /**
//     * Method service used to calculate the distance in miles, between user's
//     * location and attraction location.
//     *
//     * @param Location loc1
//     * @param Location loc2
//     * @return double statuteMiles
//     */
//    public double getDistance(Location loc1, Location loc2) {
//        double lat1 = Math.toRadians(loc1.latitude);
//        double lon1 = Math.toRadians(loc1.longitude);
//        double lat2 = Math.toRadians(loc2.latitude);
//        double lon2 = Math.toRadians(loc2.longitude);
//
//        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
//                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
//
//        double nauticalMiles = 60 * Math.toDegrees(angle);
//        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
//        return statuteMiles;
//    }
//
//}
