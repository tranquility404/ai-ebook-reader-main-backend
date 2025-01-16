package com.tranquility.ebookreader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class TestComponent {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @PostConstruct
    public void demo() {
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
