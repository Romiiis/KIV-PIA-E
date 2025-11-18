package com.romiiis.infrastructure.mail;


import com.romiiis.domain.Project;
import com.romiiis.port.IMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@RequiredArgsConstructor
public class EmailService implements IMailService {


    private final JavaMailSender emailSender;



    @Override
    public void sendEmailToCustomer(Project project, String subject, String text) {

        if (project.getCustomer() == null) {
            log.warn("No customer assigned to project with id {}. Email not sent.", project.getId());
            return;
        }

        log.info("Sending email to customer with subject {}", subject);
        String customerEmail = project.getCustomer().getEmailAddress();
        sendEmail(customerEmail, subject, text);
    }

    @Override
    public void sendEmailToTranslator(Project project, String subject, String text) {

        if (project.getTranslator() == null) {
            log.warn("No translator assigned to project with id {}. Email not sent.", project.getId());
            return;
        }

        log.info("Sending email to translator with subject {}", subject);
        String translatorEmail = project.getTranslator().getEmailAddress();
        sendEmail(translatorEmail, subject, text);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@linguaflow.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        emailSender.send(message);
    }


}
