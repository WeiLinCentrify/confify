package confify.service;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by Dennis on 4/30/2015.
 */
@Service
public class QRCodeService {
    @Autowired private StorageService storageService;
    public String generateQRcode(String userId) {
        ByteArrayOutputStream out = QRCode.from(userId)
                .to(ImageType.PNG).stream();
        String rootpath = "static/nodejs/public/";
        String filepath = "images/qr/" + userId + ".PNG";
        storageService.putFile(rootpath + filepath, out.toByteArray());
        return filepath;
    }
}
