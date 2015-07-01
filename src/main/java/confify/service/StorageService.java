package confify.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Dennis on 5/1/2015.
 */
@Service
public class StorageService {
    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
    public void putFile(String filePath, byte[] bytes) {
        try {
            File serverFile = new File(filePath);
            if (serverFile.exists()) serverFile.delete();
            else serverFile.getParentFile().mkdirs();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
            logger.info("Server FileRecord Location=" + serverFile.getAbsolutePath());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
