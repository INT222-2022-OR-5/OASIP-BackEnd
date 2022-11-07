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
        message.setFrom("wong.saengsurasak@gmail.com");
        message.setTo(toMail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        System.out.println("Mail Sent Successfully...");
    }
}
