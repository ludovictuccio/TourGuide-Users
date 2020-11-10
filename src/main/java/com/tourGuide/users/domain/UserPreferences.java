package com.tourGuide.users.domain;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;

import com.jsoniter.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserPreferences {

    @JsonIgnore
    private CurrencyUnit currency = Monetary.getCurrency("USD");

    @JsonIgnore
    @Getter
    @Setter
    private Money lowerPricePoint = Money.of(0, currency);

    @JsonIgnore
    @Getter
    @Setter
    private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);

    @Getter
    @Setter
    private int attractionProximity = Integer.MAX_VALUE;

    @Getter
    @Setter
    private int tripDuration = 1;

    @Getter
    @Setter
    private int ticketQuantity = 1;

    @Getter
    @Setter
    private int numberOfAdults = 1;

    @Getter
    @Setter
    private int numberOfChildren = 0;

    public UserPreferences(final int utripDuration, final int uticketQuantity,
            final int unumberOfAdults, final int unumberOfChildren) {
        super();
        this.tripDuration = utripDuration;
        this.ticketQuantity = uticketQuantity;
        this.numberOfAdults = unumberOfAdults;
        this.numberOfChildren = unumberOfChildren;
    }

}
