package com.tourGuide.users.domain;

import com.tourGuide.users.domain.dto.AttractionDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class UserReward {

    @Getter
    @Setter
    public VisitedLocation visitedLocation;

    @Getter
    @Setter
    public AttractionDto attraction;

    @Getter
    @Setter
    private int rewardPoints;

}
