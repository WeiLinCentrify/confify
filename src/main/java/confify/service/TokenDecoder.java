package confify.service;

import confify.exception.UnauthorizedException;
import confify.models.Credential;
import confify.models.User;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/**
 * Created by Dennis on 4/26/2015.
 */
public class TokenDecoder {
    public static Credential decode(String token) {
        StringTokenizer st = new StringTokenizer(token);
        if (st.hasMoreTokens()) {
            String basic = st.nextToken();
            if (basic.equalsIgnoreCase("Basic")) {
                try {
                    String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
                    int p = credentials.indexOf(":");
                    if (p != -1) {
                        String email = credentials.substring(0, p).trim();
                        String password = credentials.substring(p + 1).trim();
                        Credential credential = new Credential(email, password);
                        return credential;
                    } else {
                        throw new UnauthorizedException();
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new Error("Couldn't retrieve authentication", e);
                }
            }
        }
        else {
            throw new UnauthorizedException();
        }
        return null;
    }
}
