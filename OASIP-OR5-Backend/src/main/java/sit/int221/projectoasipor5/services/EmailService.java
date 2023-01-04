package sit.int221.projectoasipor5.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail (String toMail , String subject , String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("or5.oasip@gmail.com");
        message.setTo(toMail);
        message.setSubject(subject);
        message.setText(body);
        message.setReplyTo("noreply@intproj21.sit.kmutt.ac.th");
        mailSender.send(message);
    }
}
