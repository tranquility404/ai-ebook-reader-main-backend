package com.tranquility.ebookreader.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;
    private RestTemplate restTemplate;

    public CloudinaryService() {
        this.restTemplate = new RestTemplate();
    }

    private Cloudinary getCloudinary() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
            ));
        }
        return cloudinary;
    }

    public String uploadBookFile(String uniqueFileName, byte[] file) throws IOException {
        try {
            String fileName = "books/" + uniqueFileName + "_book";
            Map uploadResult = getCloudinary().uploader().upload(file, ObjectUtils.asMap(
                "public_id", fileName,
                "resource_type", "raw",
                "folder", "books"
            ));
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            throw new IOException("Failed to upload book file to Cloudinary", e);
        }
    }

    public InputStream downloadFile(String cloudUrl) throws IOException, InterruptedException {
        RestTemplate template = new RestTemplate();
        Resource resource = template.getForObject(cloudUrl, Resource.class);
        if (resource != null && resource.exists()) {
            return resource.getInputStream();
        } else {
            throw new IOException("Failed to fetch the file from the public URL.");
        }
    }

    public String uploadSummaryTxtToCloud(String content) {
        try {
            String uniqueFileName = UUID.randomUUID().toString();
            String fileName = "summaries/" + uniqueFileName + "_summary";
            
            Map uploadResult = getCloudinary().uploader().upload(content.getBytes(), ObjectUtils.asMap(
                "public_id", fileName,
                "resource_type", "raw",
                "folder", "summaries"
            ));
            
            return (String) uploadResult.get("public_id");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload summary to Cloudinary", e);
        }
    }

    public String uploadQuizJsonToCloud(String content) {
        try {
            String uniqueFileName = UUID.randomUUID().toString();
            String fileName = "quiz/" + uniqueFileName + "_quiz";
            
            Map uploadResult = getCloudinary().uploader().upload(content.getBytes(), ObjectUtils.asMap(
                "public_id", fileName,
                "resource_type", "raw",
                "folder", "quiz"
            ));
            
            return (String) uploadResult.get("public_id");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload quiz to Cloudinary", e);
        }
    }

    public byte[] resizeProfilePic(InputStream is) throws IOException {
        final int PROFILE_PIC_WIDTH = 150;
        final int PROFILE_PIC_HEIGHT = 150;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Thumbnails.of(is)
                .size(PROFILE_PIC_WIDTH, PROFILE_PIC_HEIGHT) // Resize the image
                .outputQuality(0.8) // Compress the image to 80% of the original quality
                .toOutputStream(byteArrayOutputStream); // Write to ByteArrayOutputStream

        return byteArrayOutputStream.toByteArray();
    }

    public String uploadProfilePicToCloud(String uniqueFileName, String contentType, byte[] bytes) throws IOException {
        if (contentType == null || !contentType.contains("image"))
            throw new RuntimeException("Invalid file type. Only image files are allowed.");

        try {
            String ext = contentType.substring(contentType.lastIndexOf('/')+1);
            String fileName = "profile-pictures/" + uniqueFileName + "_image";

            Map uploadResult = getCloudinary().uploader().upload(bytes, ObjectUtils.asMap(
                "public_id", fileName,
                "folder", "profile-pictures",
                "format", ext,
                "transformation", ObjectUtils.asMap(
                    "width", 150,
                    "height", 150,
                    "crop", "fill"
                )
            ));

            long timestamp = System.currentTimeMillis();
            String secureUrl = (String) uploadResult.get("secure_url");
            return secureUrl + "?t=" + timestamp;
        } catch (Exception e) {
            throw new IOException("Failed to upload profile picture to Cloudinary", e);
        }
    }

    public String getFileContent(String resourceId) {
        try {
            // For Cloudinary, we need to construct the URL and fetch the content
            String url = getCloudinary().url().resourceType("raw").generate(resourceId);
            
            RestTemplate template = new RestTemplate();
            Resource resource = template.getForObject(url, Resource.class);
            
            if (resource != null && resource.exists()) {
                StringBuilder fileContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }
                }
                return fileContent.toString();
            } else {
                throw new FileNotFoundException("File not found: " + resourceId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file content from Cloudinary", e);
        }
    }
}