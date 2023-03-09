package com.xxd.platform.controller;

import com.xxd.platform.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/*\
* file update and dowload*/
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.toString());
//        //original name
        String originalName = file.getOriginalFilename();

        //get suffix from original file type
        String fileType = originalName.substring(originalName.lastIndexOf("."));


        //uuid get new file name to avoid repeated name
        String fileName = UUID.randomUUID().toString() + fileType;

        //check dir, create fold if not exist
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }


        try {
            //change file into default address
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    //load image, so no return, just show the picture
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //instream, get file content
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            // outstream, write file back to the browser to show the picture
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");


            int len = 0;
            byte[] bytes = new byte[1024];
            while( (len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0,len);
                outputStream.flush();
            }

            //close resourse
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
