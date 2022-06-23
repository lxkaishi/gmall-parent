package com.atguigu.gmall.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author lxstart
 * @description
 * @create 2022-06-23 14:24
 */
public interface FileService {
    /**
     * 上传前端提交的文件到Minio
     * @param file
     * @return
     */
    String fileUpload(MultipartFile file) throws Exception;
}
