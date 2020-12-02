package com.tourGuide.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.tourGuide.users.domain.Location;
import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.VisitedLocationDto;
import com.tourGuide.users.proxies.MicroserviceGpsProxy;
import com.tourGuide.users.proxies.MicroserviceRewardsProxy;
import com.tourGuide.users.services.UserService;
import com.tourGuide.users.tracker.Tracker;

@SpringBootTest
public class TrackerTest {

    @Autowired
    public UserService userService;

    @MockBean
    public Tracker tracker;

    @MockBean
    private MicroserviceGpsProxy microserviceGpsProxy;

    @MockBean
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    private static final String USER_TEST = "test";

    @Test
    @Tag("Tracker")
    @DisplayName("Tracker - OK - VisitedLocation +1")
    public void givenUserWithThreeVisitedLMocations_whenWaitFiveMinutes_thenReturnFourVisitedLocations()
            throws InterruptedException {
        // GIVEN
        User user = new User(UUID.randomUUID(), "test", "0299887744",
                "email@gmail.com");
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                    new Location(-45.36665d, 2.365545d), new Date()));
        });
        userService.addUser(user);
        assertThat(userService.getUser(USER_TEST).getVisitedLocations().size())
                .isEqualTo(3);

        // WHEN
        when(microserviceGpsProxy.getUserInstantLocation(USER_TEST))
                .thenReturn(new VisitedLocationDto(user.getUserId(), -45.36665d,
                        2.365545d, new Date()));
        userService.trackUserLocation(user);
        tracker.runForTest();
        tracker.stopTracking();

        // THEN
        assertThat(userService.getUser(USER_TEST).getVisitedLocations().size())
                .isEqualTo(4);// changed, +1
    }
}
