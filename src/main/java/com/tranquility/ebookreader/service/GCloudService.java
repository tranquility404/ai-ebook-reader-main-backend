package com.tranquility.ebookreader.service;

import com.google.cloud.storage.*;
import com.tranquility.ebookreader.utils.AuthUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.UUID;

@Service
public class GCloudService {

    @Value("${gcloud.bucket.name}")
    private String bucketName;

    private Storage storage;
    private RestTemplate restTemplate;

    public GCloudService() {
        // Initialize the GCS client
        this.restTemplate = new RestTemplate();
        this.storage = StorageOptions.getDefaultInstance().getService();
        System.out.println(bucketName);
        System.out.println(storage.list());
    }

    public String uploadBookFile(String uniqueFileName, byte[] file) throws IOException {
        Bucket bucket = storage.get(bucketName);

        String fileName = "books/" + uniqueFileName + "_book.epub";
        Blob blob = bucket.create(fileName, file);
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
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
        Bucket bucket = storage.get(bucketName);
        String uniqueFileName = UUID.randomUUID().toString();
        String fileName = "summaries/" + uniqueFileName + "_summary.txt";
        bucket.create(fileName, content.getBytes());

        return fileName;
    }

    public String uploadQuizJsonToCloud(String content) {
        Bucket bucket = storage.get(bucketName);
        String uniqueFileName = UUID.randomUUID().toString();
        String fileName = "quiz/" + uniqueFileName + "_quiz.json";
        bucket.create(fileName, content.getBytes());

        return fileName;
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

        String ext = contentType.substring(contentType.lastIndexOf('/')+1);
//        String uniqueFileName = UUID.randomUUID().toString();
        String fileName = "profile-pictures/" + uniqueFileName + "_image." + ext;

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, bytes);
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        long timestamp = System.currentTimeMillis();
        return String.format("https://storage.googleapis.com/%s/%s?t=%d", bucketName, fileName, timestamp);
//        return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
    }

    public String getFileContent(String resourceId) {
        Bucket bucket = storage.get(bucketName);

        Blob blob = bucket.get(resourceId);
        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(blob.getContent())))) {
            if (!blob.exists())
                throw new FileNotFoundException("File not found in bucket: " + resourceId);

            String line;
            while ((line = reader.readLine()) != null)
                fileContent.append(line).append("\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileContent.toString();
    }
}
