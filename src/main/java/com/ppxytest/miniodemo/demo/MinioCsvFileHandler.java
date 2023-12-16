package com.ppxytest.miniodemo.demo;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.ppxytest.miniodemo.mapper.UserMinioMapper;
import com.ppxytest.miniodemo.model.UserMinio;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MinioCsvFileHandler {

    private final MinioClient minioClient;
    private final UserMinioMapper userMinioMapper;

    public MinioCsvFileHandler(String endpoint, String accessKey, String secretKey, SqlSession sqlSession) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.userMinioMapper = sqlSession.getMapper(UserMinioMapper.class);
    }

    public static void main(String[] args) throws CsvValidationException, MinioException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        Reader reader;
        SqlSessionFactory sqlSessionFactory;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing SqlSessionFactory. Cause: " + e);
        }

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            MinioCsvFileHandler handler = new MinioCsvFileHandler(
                    MinioConstant.END_POINT,
                    MinioConstant.MINIO_ROOT_USER,
                    MinioConstant.MINIO_ROOT_PASSWORD,
                    sqlSession
            );
            handler.readCsvFromMinio(MinioConstant.BUCKET_NAME, "sample_init.csv");
            sqlSession.commit();  // 手动提交事务
        }
    }

    public void readCsvFromMinio(String bucketName, String objectName) throws MinioException, NoSuchAlgorithmException, InvalidKeyException, IOException, CsvValidationException {
        // Generate pre-signed URL for object download
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(1, TimeUnit.HOURS)
                        .build()
        );

        // Download object using the pre-signed URL
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (Reader reader = new InputStreamReader(connection.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            // Skip the header line
            csvReader.readNext();

            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    // Initialize UserMinio object
                    UserMinio userMinio = new UserMinio(nextRecord[0], Integer.parseInt(nextRecord[1]), nextRecord[2]);
                    // Insert into database
                    userMinioMapper.insertUserMinio(userMinio);

                    // Print for debugging or logging
                    for (String cell : nextRecord) {
                        System.out.print(cell + "\t");
                    }
                    System.out.println();
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid row: " + Arrays.toString(nextRecord));
                }
            }
        }
    }
}