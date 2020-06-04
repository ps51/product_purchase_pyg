package com.pinyougou.manager.controller;

import com.entity.pojo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

/**
 * @program: pinyougou-parent
 * @description: ${description}
 * @author: Mr.Cherry
 * @create: 2019-11-07 21:05
 **/
@RestController
public class UploadController {


    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        //获得文件名
        String originalFilename = file.getOriginalFilename();
        //得到扩展名
        String fileName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String fileId = client.uploadFile(file.getBytes(), fileName);
            //图片完整地址
            String url = FILE_SERVER_URL + fileId;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"上传失败");
    }




}
