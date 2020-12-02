package com.tourGuide.users.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tourGuide.users.domain.User;
import com.tourGuide.users.domain.UserReward;
import com.tourGuide.users.domain.VisitedLocation;
import com.tourGuide.users.domain.dto.UserRewardsDto;
import com.tourGuide.users.proxies.MicroserviceGpsProxy;
import com.tourGuide.users.proxies.MicroserviceRewardsProxy;
import com.tourGuide.users.repository.InternalTestHelper;
import com.tourGuide.users.repository.InternalUserRepository;
import com.tourGuide.users.services.ITripPricerService;
import com.tourGuide.users.services.IUserService;
import com.tourGuide.users.tracker.Tracker;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    public IUserService userService;

    @Autowired
    public ITripPricerService tripPricerService;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @MockBean
    private Tracker tracker;

    @MockBean
    private MicroserviceGpsProxy microserviceGpsProxy;

    @MockBean
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    private static final String URI_POST_ADD_USER = "/user";
    private static final String URI_UPDATE_PREFERENCES = "/user/updatePreferences";
    private static final String URI_GET_LOCATION = "/user/getLocation";
    private static final String URI_GET_ALL_LOCATION = "/user/getAllUsersLocations";
    private static final String URI_GET_ALL_USERNAMES = "/user/getAllUsernames";
    private static final String URI_GET_USER = "/user/getUser";
    private static final String URI_GET_FIVE_CLOSEST_ATTRACTIONS = "/user/getTheFiveClosestAttractions";
    private static final String URI_GET_REWARDS = "/user/getRewards";
    private static final String URI_GET_ALL_USER_REWARDS = "/user/getAllUserRewardsPoints";
    private static final String URI_GET_TRIP_DEALS = "/user/getTripDeals";

    private static final String USER_TEST_1 = "internalUser1";

    private static final String PARAM_USERNAME = "userName";

    @BeforeEach
    public void setUpPerTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        InternalTestHelper.setInternalUserNumber(2);
    }

    @Test
    @Tag("getTripDeals")
    @DisplayName("Get TripDeals - Ok")
    public void givenUser_whenGetTripDeals_thenReturnOk() throws Exception {
        User user = new User(UUID.randomUUID(), "internalUser1", "000",
                "jon@tourGuide.com");
        internalUserRepository.internalUserMap.put("internalUser1", user);

        List<VisitedLocation> allUsersVisitedLocations = new ArrayList<>();
        List<UserReward> allUsersRewards = new ArrayList<>();
        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                "internalUser1", allUsersVisitedLocations, allUsersRewards);

        when(microserviceRewardsProxy.calculateRewards("internalUser1"))
                .thenReturn(userRewardsDto);

        this.mockMvc
                .perform(get(URI_GET_TRIP_DEALS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, USER_TEST_1))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("getTripDeals")
    @DisplayName("Get TripDEals - Error - User not found")
    public void givenUnknowUsername_whenGetTripDeals_thenReturnBadRequest()
            throws Exception {
        User user = new User(UUID.randomUUID(), "internalUser1", "000",
                "jon@tourGuide.com");
        internalUserRepository.internalUserMap.put("internalUser1", user);

        List<VisitedLocation> allUsersVisitedLocations = new ArrayList<>();
        List<UserReward> allUsersRewards = new ArrayList<>();
        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                "internalUser1", allUsersVisitedLocations, allUsersRewards);

        when(microserviceRewardsProxy.calculateRewards("internalUser1"))
                .thenReturn(userRewardsDto);

        this.mockMvc
                .perform(get(URI_GET_TRIP_DEALS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "unknow"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Tag("getAllUserRewardsPoints")
    @DisplayName("Get All User Rewards Points - Ok")
    public void givenUser_whenGetAllRewardsPoints_thenReturnOk()
            throws Exception {
        User user = new User(UUID.randomUUID(), "internalUser1", "000",
                "jon@tourGuide.com");
        internalUserRepository.internalUserMap.put("internalUser1", user);

        List<VisitedLocation> allUsersVisitedLocations = new ArrayList<>();
        List<UserReward> allUsersRewards = new ArrayList<>();
        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                "internalUser1", allUsersVisitedLocations, allUsersRewards);

        when(microserviceRewardsProxy.calculateRewards("internalUser1"))
                .thenReturn(userRewardsDto);

        this.mockMvc
                .perform(get(URI_GET_ALL_USER_REWARDS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, USER_TEST_1))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("getAllUserRewardsPoints")
    @DisplayName("Get All User Rewards Points - Error - User not found")
    public void givenUnknowUsername_whenGetAllRewardsPoints_thenReturnBadRequest()
            throws Exception {
        this.mockMvc
                .perform(get(URI_GET_ALL_USER_REWARDS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "unknow"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Tag("getRewards")
    @DisplayName("Get Rewards - Ok")
    public void givenUser_whenGetRewards_thenReturnOk() throws Exception {
        User user = new User(UUID.randomUUID(), "internalUser1", "000",
                "jon@tourGuide.com");
        internalUserRepository.internalUserMap.put("internalUser1", user);

        List<VisitedLocation> allUsersVisitedLocations = new ArrayList<>();
        List<UserReward> allUsersRewards = new ArrayList<>();
        UserRewardsDto userRewardsDto = new UserRewardsDto(user.getUserId(),
                "internalUser1", allUsersVisitedLocations, allUsersRewards);

        when(microserviceRewardsProxy.calculateRewards("internalUser1"))
                .thenReturn(userRewardsDto);

        this.mockMvc
                .perform(get(URI_GET_REWARDS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, USER_TEST_1))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("getRewards")
    @DisplayName("Get Rewards - Error - User not found")
    public void givenUnknowUser_whenGetRewards_thenReturnNotFound()
            throws Exception {
        this.mockMvc
                .perform(get(URI_GET_REWARDS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "unknow"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Tag("getUser")
    @DisplayName("Get user - Ok")
    public void givenUser_whenGetWhithHisUsername_thenReturnOk()
            throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, USER_TEST_1))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("getUser")
    @DisplayName("Get user - Error 400")
    public void givenUnknowUsername_whenGetUser_thenReturnNotfound()
            throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "UNKNOW"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Tag("GetTheFiveClosestAttractions")
    @DisplayName("Get The Five Closest Attractions- OK")
    public void givenUserWithVisitedLocations_whenGetTheFiveClosestAttractions_thenReturnOk()
            throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get(URI_GET_FIVE_CLOSEST_ATTRACTIONS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, USER_TEST_1))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("getTheFiveClosestAttractions")
    @DisplayName("Get The Five Closest Attractions- Error - Username not found")
    public void givenUnknowUsername_whenGetTheFiveClosestAttractions_thenReturnBadRequest()
            throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get(URI_GET_FIVE_CLOSEST_ATTRACTIONS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "unknow"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Tag("getTheFiveClosestAttractions")
    @DisplayName("Get The Five Closest Attractions- Error - User without visited location")
    public void givenUserWithoutVisitedLocation_whenGetTheFiveClosestAttractions_thenReturnBadRequest()
            throws Exception {
        userService.addUser(
                new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com"));
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get(URI_GET_FIVE_CLOSEST_ATTRACTIONS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "unknow"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Tag("updatePreferences")
    @DisplayName("Update UserPreferences - Ok")
    public void givenValidUsername_whenUpdatePreferences_thenReturnOk()
            throws Exception {

        String jsonContent = "{\"tripDuration\":\"3\",\"ticketQuantity\":\"7\", \"numberOfAdults\": \"2\", \"numberOfChildren\": \"5\"}";

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI_UPDATE_PREFERENCES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, USER_TEST_1)
                        .content(jsonContent))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("updatePreferences")
    @DisplayName("Update UserPreferences - Error 400 - invalid username")
    public void givenInvalidUsername_whenUpdatePreferences_thenReturnNotfound()
            throws Exception {

        String jsonContent = "{\"tripDuration\":\"3\",\"ticketQuantity\":\"7\", \"numberOfAdults\": \"2\", \"numberOfChildren\": \"5\"}";

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI_UPDATE_PREFERENCES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "unknow").content(jsonContent))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Tag("GetLocation")
    @DisplayName("Get Location- OK - Valid username")
    public void givenValidUsername_whenGetLocation_thenReturnOk()
            throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_LOCATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, USER_TEST_1))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("GetLocation")
    @DisplayName("Get Location- Error - Unknow username")
    public void givenInvalidUsername_whenGetLocation_thenReturnNotFound()
            throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_LOCATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "unknow"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Tag("GetLocation")
    @DisplayName("Get Location- Error - User without visited location")
    public void givenUserWithoutVisitedLocation_whenGetLocation_thenReturnNotFound()
            throws Exception {

        User user = new User(UUID.randomUUID(), "Usertest", "000",
                "jon@tourGuide.com");
        internalUserRepository.internalUserMap.put("Usertest", user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_LOCATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "Usertest"))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @Tag("GetAllUsersLocations")
    @DisplayName("Get All UsersLocation- OK")
    public void givenUsers_whenGetAllLocations_thenReturnOk() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_ALL_LOCATION)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("AddUser")
    @DisplayName("Add User - OK - Valid username")
    public void givenValidUsername_whenAddUser_thenReturnOk() throws Exception {

        String jsonContent = "{\"userName\":\"usertest\", \"phoneNumber\":\"123456789\",\"emailAddress\": \"email@email.com\"}";

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI_POST_ADD_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("AddUser")
    @DisplayName("Add User - Error - Already existing username username")
    public void givenInvalidUsername_whenAddUser_thenReturnBadRequest()
            throws Exception {

        String jsonContent = "{\"userName\":\"usernameTest\", \"phoneNumber\":\"123456789\",\"emailAddress\": \"email@email.com\"}";

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI_POST_ADD_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI_POST_ADD_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Tag("getAllUsernames")
    @DisplayName("Get All usernames - Ok - Empty list")
    public void givenEmptyList_whenGetAllUsernames_thenReturnOk()
            throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_ALL_USERNAMES)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("getAllUsernames")
    @DisplayName("Get All usernames - Ok - 1 user")
    public void givenOneUser_whenGetAllUsernames_thenReturnOk()
            throws Exception {

        String jsonContent = "{\"userName\":\"usernameTest\", \"phoneNumber\":\"123456789\",\"emailAddress\": \"email@email.com\"}";

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI_POST_ADD_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_ALL_USERNAMES)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

}
