package com.tourGuide.users.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class ClosestAttraction {

    @Getter
    @Setter
    public String attractionName;

    @Getter
    @Setter
    public Location attractionLocation;

    @Getter
    @Setter
    public Location userLocation;

    @Setter
    public double distanceInMiles;

    @Getter
    @Setter
    public int attractionRewardsPoints;

    /**
     * @return a double with 3 digits precision
     */
    public double getDistanceInMiles() {
        return Math.round(distanceInMiles * 1000.0) / 1000.0;
    }
}
