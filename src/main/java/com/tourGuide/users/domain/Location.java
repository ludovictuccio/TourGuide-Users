package com.tourGuide.users.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Getter
    @Setter
    public double latitude;

    @Getter
    @Setter
    public double longitude;

}
