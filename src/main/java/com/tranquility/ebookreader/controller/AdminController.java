package com.tranquility.ebookreader.controller;

import com.tranquility.ebookreader.model.BookInfo;
import com.tranquility.ebookreader.model.User;
import com.tranquility.ebookreader.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/ban/{userId}")
    public ResponseEntity<?> banUser(@PathVariable String userId) {
        User user = adminService.banUser(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/books")
    public ResponseEntity<?> getAllPGs() {
        List<BookInfo> books = adminService.getAllBooks();
        return ResponseEntity.ok(books);
    }
}

