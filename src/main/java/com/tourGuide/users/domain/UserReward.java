package com.tourGuide.users.domain;

import com.tourGuide.users.domain.dto.AttractionDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class UserReward {

    public VisitedLocation visitedLocation;

    public AttractionDto attraction;

    @Getter
    @Setter
    private int rewardPoints;

    public UserReward(VisitedLocation visitedLocation,
            AttractionDto attraction) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
    }

}
