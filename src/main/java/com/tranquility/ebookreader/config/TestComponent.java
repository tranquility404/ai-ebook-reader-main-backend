package com.tranquility.ebookreader.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@Component
public class TestComponent {

    @Value("${cors.allowed-origins}")
    private String origins;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @PostConstruct
    public void demo() throws IOException {
        System.out.println(Arrays.asList(origins.split(",")));
        System.out.println(maxFileSize);
        System.out.println(maxRequestSize);
        try {
            // Get the local host InetAddress object
            InetAddress inetAddress = InetAddress.getLocalHost();

            // Print the IP address
            System.out.println("IP Address: " + inetAddress.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        System.out.println(maxHttpPostSize);
    }
}
