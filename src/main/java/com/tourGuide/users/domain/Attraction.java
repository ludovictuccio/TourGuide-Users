package com.tourGuide.users.domain;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class Attraction {

    @Getter
    @Setter
    public Location location;

    @Getter
    @Setter
    public String attractionName;

    @Getter
    @Setter
    public String city;

    @Getter
    @Setter
    public String state;

    @Getter
    @Setter
    public UUID attractionId;

}
