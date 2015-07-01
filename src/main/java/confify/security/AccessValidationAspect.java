package confify.security;

import confify.exception.UnauthorizedException;
import confify.models.AccountType;
import confify.models.Admin;
import confify.models.Credential;
import confify.models.User;
import confify.repositories.AdminRepository;
import confify.repositories.UserRepository;
import confify.service.TokenDecoder;
import org.apache.tomcat.util.codec.binary.Base64;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Created by Dennis on 4/26/2015.
 */
@Aspect
@Component
public class AccessValidationAspect {
    private static final Logger logger = LoggerFactory.getLogger(AccessValidationAspect.class);
    @Autowired private UserRepository userRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private PasswordEncoder pwdEncoder;

    @Before("execution(* confify.controllers.*.*(..)) && !execution(* confify.controllers.AccountController.registerUser(..)) " +
            "&& !execution(* confify.controllers.AccountController.registerAdmin(..)) "+
            "&& !execution(* confify.controllers.AccountController.login(..))")
    public void basicHttpAuth(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String authHeader = (String)args[0];
        if (authHeader == null) throw new UnauthorizedException();
        logger.debug("Basic Http Auth Token: " + authHeader);
        Credential credential = null;
        try {
            credential = TokenDecoder.decode(authHeader);
        }
        catch(Exception e) {
            throw new UnauthorizedException();
        }
        String accountType = (String)args[1];
        if (accountType.equals(AccountType.ADMIN)) {
            Admin admin = adminRepository.getAdminByEmail(credential.getUsername());
            if (admin == null || !pwdEncoder.matches(credential.getPassword(), admin.getPassword())) {
                throw new UnauthorizedException();
            }
        }
        else {
            User user = userRepository.getUserByEmail(credential.getUsername());
            if (user == null || !pwdEncoder.matches(credential.getPassword(), user.getPassword())) {
                throw new UnauthorizedException();
            }
        }

    }
}
