package com.jilnash.swecsci361.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class S3Service {

    @Autowired
    private S3Client s3;

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
                    .key(String.format(String.valueOf(i++), Objects.requireNonNull(f.getContentType()).split("/")[1]))
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

    public Resource getFile(String bucketName, String key) {

        return new InputStreamResource(
                s3.getObject(
                        GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build()
                )
        );
    }

    public List<String> getFilesNames(String bucketName) {

        return s3.listObjectsV2(
                ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .build()
        ).contents().stream().map(S3Object::key).collect(Collectors.toList());
    }
}
