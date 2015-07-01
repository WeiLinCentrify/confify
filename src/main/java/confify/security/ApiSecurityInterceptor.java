package confify.security;

import confify.models.User;
import confify.repositories.UserRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import org.slf4j.LoggerFactory;


/**
 * Created by Dennis on 4/22/2015.
 */
public class ApiSecurityInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ApiSecurityInterceptor.class);
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder pwdEncoder;

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object obj, Exception ex)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object obj, ModelAndView mvc)
            throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Exception {
        String authHeader = req.getHeader("authorization");
        if (anthenticateService(authHeader)) {
            return true;
        }
        else {
            resp.sendError(401, "unauthorized api access");
            return false;
        }
    }

    public boolean anthenticateService(String token) {
        if (token != null) {
            try {
                String credentials = new String(Base64.decodeBase64(token), "UTF-8");
                int p = credentials.indexOf(":");
                if (p != -1) {
                    String email = credentials.substring(0, p).trim();
                    String password = credentials.substring(p + 1).trim();
                    User user = userRepository.getUserByEmail(email);
                    if (user != null && pwdEncoder.matches(password, user.getPassword()) ) {
                        return true;
                    }
                    else {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (UnsupportedEncodingException e) {
                return false;
            }
        }
        else {
            return false;
        }
    }
}
