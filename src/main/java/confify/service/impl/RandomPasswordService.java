package confify.service.impl;

import confify.service.PasswordService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Dennis on 5/5/2015.
 */
@Service
public class RandomPasswordService implements PasswordService {
    public String generatePassword() {
        SecureRandom random = new SecureRandom();
        String generatedPwd = new BigInteger(40, random).toString(32);
        return generatedPwd;
    }
}
