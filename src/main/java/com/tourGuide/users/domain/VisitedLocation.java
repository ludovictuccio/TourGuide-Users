package com.tourGuide.users.domain;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class VisitedLocation {

    @Getter
    @Setter
    public UUID userId;

    @Getter
    @Setter
    public Location location;

    @Getter
    @Setter
    public Date timeVisited;

}
