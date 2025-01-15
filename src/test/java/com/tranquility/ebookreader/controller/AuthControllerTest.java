package com.tranquility.ebookreader.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquility.ebookreader.dto.AuthenticationRequest;
import com.tranquility.ebookreader.dto.UserDto;
import com.tranquility.ebookreader.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto mockUserDto;

    @BeforeEach
    void setUp() {
        mockUserDto = new UserDto();
        mockUserDto.setId("1");
        mockUserDto.setEmail("test@example.com");
        mockUserDto.setRoles(Set.of("USER"));
        mockUserDto.setBanned(false);
    }

    @Test
    void testRegister() throws Exception {
        AuthenticationRequest registerRequest = new AuthenticationRequest();
        registerRequest.setPassword("password");
        registerRequest.setEmail("test@example.com");

        when(authService.register(any(AuthenticationRequest.class))).thenReturn(mockUserDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testLogin() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setPassword("password");

        when(authService.login(any(AuthenticationRequest.class))).thenReturn(String.valueOf(mockUserDto));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}

