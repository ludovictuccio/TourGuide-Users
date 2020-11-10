package com.tourGuide.users.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tourGuide.users.helper.InternalTestHelper;
import com.tourGuide.users.services.UserService;

import gpsUtil.GpsUtil;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    public UserService userService;

    @Autowired
    private GpsUtil gpsUtil;

    private static final String URI_POST_ADD_USER = "/user";
    private static final String URI_GET_LOCATION = "/user/getLocation";
    private static final String URI_GET_ALL_USERNAMES = "/user/getAllUsernames";
    private static final String URI_GET_USER = "/user/getUser";
    private static final String URI_UPDATE_PREFERENCES = "/user/updatePreferences";

    private static final String USER_TEST_1 = "internalUser1";

    private static final String PARAM_USERNAME = "userName";

    @BeforeEach
    public void setUpPerTest() {
        gpsUtil = new GpsUtil();
        userService = new UserService(gpsUtil);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        InternalTestHelper.setInternalUserNumber(2);
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
    @DisplayName("Update UserPreferences - Error 404 - invalid username")
    public void givenInvalidUsername_whenUpdatePreferences_thenReturnNotfound()
            throws Exception {

        String jsonContent = "{\"tripDuration\":\"3\",\"ticketQuantity\":\"7\", \"numberOfAdults\": \"2\", \"numberOfChildren\": \"5\"}";

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI_UPDATE_PREFERENCES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_USERNAME, "unknow").content(jsonContent))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound()).andReturn();
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
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound()).andReturn();
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

    @Test
    @Tag("getUser")
    @DisplayName("Get user - Ok")
    public void givenUser_whenGetWhithHisUsernam_thenReturnOk()
            throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userName", "internalUser1"))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Tag("getUser")
    @DisplayName("Get user - Error 404")
    public void givenUnknowUsername_whenGetUser_thenReturnNotfound()
            throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI_GET_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userName", "jon"))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound()).andReturn();
    }

}
