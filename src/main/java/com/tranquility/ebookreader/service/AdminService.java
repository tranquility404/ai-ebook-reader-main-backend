package com.tranquility.ebookreader.service;

import com.tranquility.ebookreader.model.BookInfo;
import com.tranquility.ebookreader.model.User;
import com.tranquility.ebookreader.repository.BookRepository;
import com.tranquility.ebookreader.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public AdminService(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public User banUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        return userRepository.save(user);
    }

    public ResponseEntity<?> removeBook(String pgId) {
        BookInfo book = bookRepository.findById(pgId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        bookRepository.delete(book);
        return ResponseEntity.ok().build();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<BookInfo> getAllBooks() {
        return bookRepository.findAll();
    }
}

