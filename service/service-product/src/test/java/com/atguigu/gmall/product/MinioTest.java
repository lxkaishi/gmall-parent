package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

/**
 * @author lxstart
 * @description
 * @create 2022-06-23 11:25
 */
public class MinioTest {

    @Test
    void testUpload(){
        try {
            //1、使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(
                    "http://192.168.30.100:9000",
                    "admin",
                    "admin123456");
            System.out.println(minioClient);

            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists("gmall");
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket("gmall");
            }

            //3、上传
            FileInputStream stream = new FileInputStream("C:\\Users\\xfyy\\Pictures\\Camera Roll\\1.png");
            //上传的一些参数项设置
            PutObjectOptions options = new PutObjectOptions(stream.available(),-1L);
            options.setContentType("image/jpeg");
            minioClient.putObject("gmall","1.png",stream,options);

            //http://192.168.200.100:9000/gmall/1.jpg
            System.out.println("上传成功：访问地址：http://192.168.30.100:9000/gmall/1.png");
        } catch(Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
