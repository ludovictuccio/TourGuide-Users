package com.tourGuide.users.domain.dto;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class VisitedLocationDto {

    @Getter
    @Setter
    public UUID userId;

    @Getter
    @Setter
    public double latitude;

    @Getter
    @Setter
    public double longitude;

    @Getter
    @Setter
    public Date timeVisited;

}
