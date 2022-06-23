package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author lxstart
 * @description
 * @create 2022-06-23 14:25
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    MinioProperties minioProperties;

    @Override
    public String fileUpload(MultipartFile file) throws Exception {

        //1、准备上传
        String filename = UUID.randomUUID().toString().replace("-","")
                + "_" + file.getOriginalFilename();

        PutObjectOptions options = new PutObjectOptions(file.getSize(), -1);
        options.setContentType(file.getContentType());

        minioClient.putObject(minioProperties.getBucketName(),filename,file.getInputStream(),options);

        //2、返回这个资源的访问路径  minio服务器地址+
        //http://192.168.30.100:9000/gmall/123.jpg
        String url =  minioProperties.getEndpoint()+"/"+minioProperties.getBucketName()+"/"+filename;
        return url;
    }
}
