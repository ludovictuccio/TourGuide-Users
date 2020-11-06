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

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ObjectMapper objectMapper;

    @Autowired
    public UserService userService;

    @Autowired
    private GpsUtil gpsUtil;

    private static final String URI_GET_LOCATION = "/user/getLocation";

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
}
