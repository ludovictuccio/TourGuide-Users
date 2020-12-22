package com.tourGuide.users.domain.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class ProviderDto {

    @Getter
    @Setter
    public String name;

    @Setter
    public double price;

    @Getter
    @Setter
    public UUID tripId;

    /**
     * @return a double with 2 digits precision
     */
    public double getPrice() {
        return Math.round(price * 100.0) / 100.0;
    }

}
