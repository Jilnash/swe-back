package com.jilnash.swecsci361.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    @Autowired
    private S3Client s3;

    @Autowired
    private S3Presigner s3Presigner;

    public void createBucket(String bucketName) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();
        s3.createBucket(createBucketRequest);
    }

    public void putFiles(List<MultipartFile> files, String bucketName) throws Exception {

        int i = 0;
        for (MultipartFile f : files) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(i++ + "." + f.getContentType().split("/")[1])
                    .build();

            s3.putObject(
                    putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromByteBuffer(ByteBuffer.wrap(f.getBytes()))
            );
        }
    }

    public void deleteFiles(String bucketName) {

        List<S3Object> objects = s3.listObjectsV2(
                ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .build()
        ).contents();

        for (S3Object o : objects) {

            System.out.println(o.key());
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(o.key())
                    .build();
            s3.deleteObject(deleteObjectRequest);
        }
    }

    public void deleteBucket(String bucketName) {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();
        s3.deleteBucket(deleteBucketRequest);
    }

    public String getFirstFileName(String bucketName) {

        List<S3Object> objects = s3.listObjectsV2(
                ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .maxKeys(1)
                        .build()
        ).contents();

        if (objects.isEmpty())
            return null;

        return objects.get(0).key();
    }

    public URL getFileURL(String bucketName, String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .responseContentDisposition("inline")
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(getObjectPresignRequest).url();
    }

    public List<URL> getFileURLs(String bucketName) {

        return s3.listObjectsV2(
                ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .build()
        ).contents().stream().map(s3Object -> {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Object.key())
                    .responseContentDisposition("inline")
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(getObjectPresignRequest).url();
        }).collect(Collectors.toList());
    }
}
