package com.sbecomm.modernized.common.application.port;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStoragePort {
    /**
     * Stores a file and returns its access path or URL.
     *
     * @param file the multipart file to store
     * @return the path or URL where the file can be accessed
     * @throws IOException if there is an error storing the file
     */
    String storeFile(MultipartFile file) throws IOException;
}
