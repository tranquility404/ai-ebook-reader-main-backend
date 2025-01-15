package com.tranquility.ebookreader.service;

import com.tranquility.ebookreader.dto.AuthenticationRequest;
import com.tranquility.ebookreader.dto.UpdateUserRequest;
import com.tranquility.ebookreader.dto.UserDto;
import com.tranquility.ebookreader.model.Roles;
import com.tranquility.ebookreader.model.User;
import com.tranquility.ebookreader.repository.UserRepository;
import com.tranquility.ebookreader.utils.AuthUtils;
import com.tranquility.ebookreader.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwt;

    public UserService(MongoTemplate mongoTemplate, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public void addUser(AuthenticationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton(Roles.USER.name()));
        user.setBanned(false);

        userRepository.save(user);
    }

    public void updateUser(UpdateUserRequest request) {
        Optional<User> optional = userRepository.findByEmail(Objects.requireNonNull(AuthUtils.getUsername()));
        if (optional.isEmpty()) throw new RuntimeException("User not found");
        User user = optional.get();
        if (request.getName() != null)
            user.setName(request.getName());
        if (request.getDob() != null)
            user.setDob(request.getDob());
        if (request.getCountry() != null)
            user.setCountry(request.getCountry());
        userRepository.save(user);
    }

    public void updateProfilePicCloudUrl(String profilePicCloudUrl) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(AuthUtils.getUsername()));

        Update update = new Update();
        update.set("profilePicCloudUrl", profilePicCloudUrl);

        mongoTemplate.updateFirst(query, update, User.class);
    }

    public String login(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return jwt.generateToken(user.getEmail());
    }

    public UserDto getUserInfo() {
        Optional<User> optional = userRepository.findByEmail(AuthUtils.getUsername());
        if (optional.isEmpty()) throw new RuntimeException("User not found");
        return convertToDto(optional.get());
    }

    public String getProfilePicCloudUrl() {
        User user = userRepository.findUserByEmailWithProfilePicCloudUrl(AuthUtils.getUsername());
        if (user == null) throw new RuntimeException("User not found");
        return user.getProfilePicCloudUrl();
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setProfilePicCloudUrl(user.getProfilePicCloudUrl());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDob(user.getDob());
        dto.setCountry(user.getCountry());
        return dto;
    }
}

