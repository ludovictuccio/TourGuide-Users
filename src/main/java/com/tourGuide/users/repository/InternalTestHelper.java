package com.tourGuide.users.repository;

import org.springframework.beans.factory.annotation.Value;

public class InternalTestHelper {

    public InternalTestHelper() {
    }

    @Value("${application.number.users}")
    private int internalUserNumber;

    public void setInternalUserNumber(final int internalUserNumb) {
        this.internalUserNumber = internalUserNumb;
    }

    public int getInternalUserNumber() {
        return internalUserNumber;
    }
}
