package com.java.electro.store.service.impl;

import com.java.electro.store.exception.BadApiRequest;
import com.java.electro.store.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadImage(MultipartFile file, String path) throws IOException {

        //abc.png
        String originalFilename = file.getOriginalFilename();
        logger.info("Filename : {} ",originalFilename);

        // generating random name of file to save in db
        String filename = UUID.randomUUID().toString();

        // getting extension (abc.png) => (png) //to save original extension we need to extract extension
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileNameWithExtension = filename + extension ;

        // path + separator(\) + abc.png
        // at this path w are going to upload image
         String fullPathWithFileName = path + fileNameWithExtension;
        logger.info("Full path With file Name is " + fullPathWithFileName);
         if(extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg")){
             // file save
             File folder = new File(path);
                if(! folder.exists()){
                    // create folder
                    folder.mkdirs();
                }

             // upload file
             Files.copy(file.getInputStream() , Paths.get(fullPathWithFileName));

             return fileNameWithExtension;
         }
         else {
             throw new BadApiRequest("File with this "+extension+" not Allowed..!!");
         }
    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String fullPath = path + File.separator + name;

        InputStream inputStream = new FileInputStream(fullPath);

        return inputStream;
    }
}


