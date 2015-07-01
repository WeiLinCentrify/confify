package confify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Properties;

/**
 * Created by Dennis on 4/22/2015.
 */
@Configuration
public class MainConfig {
    @Bean
    public PasswordEncoder pwdEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("confify.mail@gmail.com");
        javaMailSender.setPassword("10141986");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }

    @Bean
    public SimpleMailMessage invitaionMailMessage() {
        SimpleMailMessage invitaionMailMessage = new SimpleMailMessage();
        invitaionMailMessage.setFrom("no-reply@confify.com");
        invitaionMailMessage.setSubject("You're been invited to conference %s");
        invitaionMailMessage.setText("%s is inviting you to event %s which will take place on %s at %s. Please login" +
                "to view more details at %s with username:%s and password:%s ");
        return invitaionMailMessage;
    }
}
