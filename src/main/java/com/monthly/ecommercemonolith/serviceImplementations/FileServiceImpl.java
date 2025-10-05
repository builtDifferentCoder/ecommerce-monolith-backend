package com.monthly.ecommercemonolith.serviceImplementations;

import com.monthly.ecommercemonolith.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
     public String uploadImage(String path, MultipartFile file) throws IOException {
        //file names of current / original file
        String originalFileName=file.getOriginalFilename();
        //generate a unique file name
        String uniqueId= UUID.randomUUID().toString();
        assert originalFileName != null;
        String fileName=
                uniqueId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath=path+ File.separator+fileName;
        //check if path exists or create
        File folder=new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }
        //upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        //return fileName
        return fileName;
    }
}
