package com.ppxytest.miniodemo.demo;

import io.minio.*;
import io.minio.errors.MinioException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 上传下载文件
 */
public class MinioBucketOperations {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // 创建MinioClient对象
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(MinioConstant.END_POINT)
                    .credentials(MinioConstant.MINIO_ROOT_USER, MinioConstant.MINIO_ROOT_PASSWORD)
                    .build();

            // 定义存储桶和对象名称
            String objectName = "my-picture.jpg";
            String filePath = "E:\\project\\杭州共擎科技有限公司\\minio-demo\\src\\main\\resources\\files\\my-picture.png";

            // 上传对象到存储桶
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(MinioConstant.BUCKET_NAME)
                            .object(objectName)
                            .filename(filePath)
                            .build()
            );
            System.out.println("Uploaded object to bucket.");

            // 下载对象
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(MinioConstant.BUCKET_NAME)
                            .object(objectName)
                            .build()
            )) {
                Files.copy(stream, Path.of("E:\\project\\杭州共擎科技有限公司\\minio-demo\\src\\main\\resources\\files\\downloaded-" + objectName), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Downloaded object from bucket.");

            // 删除对象
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(MinioConstant.BUCKET_NAME)
                            .object(objectName)
                            .build()
            );
            System.out.println("Deleted object from bucket.");

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
