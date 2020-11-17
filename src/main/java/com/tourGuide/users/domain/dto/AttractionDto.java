package com.tourGuide.users.domain.dto;

import java.util.UUID;

import com.tourGuide.users.domain.Location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class AttractionDto {

    @Getter
    @Setter
    public String attractionName;

    @Getter
    public Location location;

    @Getter
    @Setter
    public String city;

    @Getter
    @Setter
    public String state;

    @Getter
    @Setter
    public UUID attractionId;

    public void setLocation(double latitude, double longitude) {
        this.location = new Location(latitude, longitude);
    }

}
