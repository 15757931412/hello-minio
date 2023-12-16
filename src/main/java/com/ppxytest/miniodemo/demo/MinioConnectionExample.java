// 文件名: MinioConnectionExample.java

package com.ppxytest.miniodemo.demo;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 初始化 client 检查桶名是否存在
 */
public class MinioConnectionExample {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println("Program started.");  // 程序开始

        try {
            System.out.println("Creating MinioClient object.");  // 创建 MinioClient 对象
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(MinioConstant.END_POINT)
                    .credentials(MinioConstant.MINIO_ROOT_USER, MinioConstant.MINIO_ROOT_PASSWORD)
                    .build();
            System.out.println("MinioClient object created.");  // MinioClient 对象已创建

            System.out.println("Checking if bucket exists.");  // 检查存储桶是否存在
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(MinioConstant.BUCKET_NAME).build());
            if (isExist) {
                System.out.println("Bucket exists.");  // 存储桶存在
            } else {
                System.out.println("Bucket does not exist.");  // 存储桶不存在
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);  // 错误发生
        }

        System.out.println("Program finished.");  // 程序结束
    }
}
