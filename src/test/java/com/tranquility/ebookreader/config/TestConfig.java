package com.tranquility.ebookreader.config;

import com.tranquility.ebookreader.repository.BookRepository;
import com.tranquility.ebookreader.repository.UserRepository;
import com.tranquility.ebookreader.service.AdminService;
import com.tranquility.ebookreader.service.BookService;
import com.tranquility.ebookreader.service.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }

    @Bean
    @Primary
    public BookRepository pgRepository() {
        return mock(BookRepository.class);
    }

    @Bean
    @Primary
    public UserService authService() {
        return mock(UserService.class);
    }

    @Bean
    @Primary
    public BookService pgService() {
        return mock(BookService.class);
    }

    @Bean
    @Primary
    public AdminService adminService() {
        return mock(AdminService.class);
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
