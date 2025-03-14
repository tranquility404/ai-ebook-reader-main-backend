package com.tranquility.ebookreader.controller;

import com.tranquility.ebookreader.dto.UpdateUserRequest;
import com.tranquility.ebookreader.model.ErrorMessage;
import com.tranquility.ebookreader.service.GCloudService;
import com.tranquility.ebookreader.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final GCloudService gCloudService;

    public UserController(UserService userService, GCloudService gCloudService) {
        this.userService = userService;
        this.gCloudService = gCloudService;
    }

    @GetMapping("/auth-status")
    public ResponseEntity<?> authStatus() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUser(@RequestParam(required = false) String email) {
        try {
            if (email != null && !email.isEmpty())
                return ResponseEntity.ok(userService.getUser(email));   // get selective details
            else
                return ResponseEntity.ok(userService.getCurrentUserInfo());    // get all details
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/profile-picture")
    public ResponseEntity<?> getProfilePicture() {
        try {
            return ResponseEntity.ok(userService.getProfilePicCloudUrl());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
        }
    }

    @PostMapping("/profile-picture")
    public ResponseEntity<?> updateProfilePicture(@RequestParam("file") MultipartFile profilePic) {
        try {
            byte[] bytes = gCloudService.resizeProfilePic(profilePic.getInputStream());
            String cloudUrl = gCloudService.uploadProfilePicToCloud(userService.getCurrentUserId(), profilePic.getContentType(), bytes);
            userService.updateProfilePicCloudUrl(cloudUrl);
            return ResponseEntity.ok(cloudUrl);
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
        }
    }

    @PostMapping("/info")
    public ResponseEntity<?> updateUserDetails(@RequestBody UpdateUserRequest request) {
        try {
            userService.updateUser(request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

}
