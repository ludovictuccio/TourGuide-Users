package com.tourGuide.users.domain;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserPreferences {

    @Getter
    @Setter
    private int attractionProximity = Integer.MAX_VALUE;

    private CurrencyUnit currency = Monetary.getCurrency("USD");

    @Getter
    @Setter
    private Money lowerPricePoint = Money.of(0, currency);

    @Getter
    @Setter
    private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);

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

}
