package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lxstart
 * @description
 * @create 2022-06-22 20:18
 */
@RestController
@RequestMapping("/admin/product")
public class FileController {

    @Autowired
    FileService fileService;

    /**
     * 文件上传到minio
     * @param file
     * @return
     */
    @PostMapping("/fileUpload")
    public Result fileUpload(HttpServletRequest request,
                             @RequestPart("file") MultipartFile file) throws Exception {

        String url = fileService.fileUpload(file);
        return Result.ok(url);
    }
}
