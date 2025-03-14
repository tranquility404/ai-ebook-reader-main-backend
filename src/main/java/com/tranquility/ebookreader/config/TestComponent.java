package com.tranquility.ebookreader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Base64;

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
        setupGoogleCloudCredentials();

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

    public void setupGoogleCloudCredentials() throws IOException {
        String base64Credentials = System.getenv("GCP_CREDENTIALS_SECRET");
        if (base64Credentials == null || base64Credentials.isEmpty())
            throw new IllegalStateException("GCP_CREDENTIALS_SECRET environment variable is not set.");

        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String credentialsJson = new String(decodedBytes);

        File tempFile = new File("ai-ebook-reader-gcloud.json");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(credentialsJson.getBytes());
        }

//        System.out.println("Google Cloud credentials stored at: " + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
    }
}
