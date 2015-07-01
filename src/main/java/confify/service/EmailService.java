package confify.service;

/**
 * Created by Dennis on 5/3/2015.
 */
public interface EmailService {
    void sendEmail(String fromEmail, String[] toEmail, String subject, String body);
}
