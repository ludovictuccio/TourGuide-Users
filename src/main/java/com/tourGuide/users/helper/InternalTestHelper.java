package com.tourGuide.users.helper;

public class InternalTestHelper {

    // Set this default up to 100,000 for testing
    private static int internalUserNumber = 100;

    public static void setInternalUserNumber(final int internalUserNumber) {
        InternalTestHelper.internalUserNumber = internalUserNumber;
    }

    public static int getInternalUserNumber() {
        return internalUserNumber;
    }
}
