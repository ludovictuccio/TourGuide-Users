package com.tourGuide.users.repository;

public class InternalTestHelper {

    public InternalTestHelper() {
    }

    // Set this default up to 100,000 for testing
    private static int internalUserNumber = 100000;

    public static void setInternalUserNumber(final int internalUserNumb) {
        InternalTestHelper.internalUserNumber = internalUserNumb;
    }

    public static int getInternalUserNumber() {
        return internalUserNumber;
    }
}
