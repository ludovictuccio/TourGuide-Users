package com.tourGuide.users.util;

import org.springframework.stereotype.Component;

import com.tourGuide.users.domain.Location;

@Component
public class DistanceCalculator {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    /**
     * Method used to calculate a distance in miles.
     *
     * @param Location loc1
     * @param Location loc2
     * @return double statuteMiles for calculate a distance
     */
    public double getDistanceInMiles(final Location loc1, final Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }
}
