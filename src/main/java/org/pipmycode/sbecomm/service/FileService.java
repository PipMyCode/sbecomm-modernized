package org.pipmycode.sbecomm.service;

import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadImage(String path, MultipartFile file) throws IOException;
}
