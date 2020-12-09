package com.offcn.shop.controller;

import com.offcn.common.utils.FastDFSClient;
import com.offcn.entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String path = fastDFSClient.uploadFile(file.getBytes(),s);
            String url = "http://192.168.188.146/"+path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }


    }

}
