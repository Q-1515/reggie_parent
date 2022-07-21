package com.reggie.controller.admin;

import com.reggie.constant.MessageConstant;
import com.reggie.result.R;
import com.reggie.utils.AliOSSUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {
    @Autowired
    private AliOSSUtil aliOSSUtil; //对象存储工具类注入


    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("文件上传:{}", file);
        String fileURL = null;

        try {
            //通过图片流读取进行图片校验
            ImageIO.read(file.getInputStream());

            //获取原文件名
            String originalFilename = file.getOriginalFilename();

            //生成唯一文件名
            String fileName = UUID.randomUUID().toString() + originalFilename;

            //存入阿里云oss对象存储返回URL
            fileURL = aliOSSUtil.upload(file.getBytes(), fileName);
        } catch (Exception e) {
            log.info("图片保存失败");
            return R.error(MessageConstant.UPLOAD_FAILED);
        }
        return R.success(fileURL);
    }


}
