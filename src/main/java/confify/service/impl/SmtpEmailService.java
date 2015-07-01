package confify.service.impl;

import confify.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Created by Dennis on 5/3/2015.
 */
@Service
public class SmtpEmailService implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(SmtpEmailService.class);
    @Autowired
    private JavaMailSender jmailSender;

    @Override
    public void sendEmail(String from, String[] to, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        jmailSender.send(simpleMailMessage);
    }
}
