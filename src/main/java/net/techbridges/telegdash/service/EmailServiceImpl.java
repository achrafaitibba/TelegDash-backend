package net.techbridges.telegdash.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;



@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String sender;
    @Value("${spring.mail.custom-name}")
    private String customName;

    public void sendEmailWithHtmlTemplate(String to, String subject, String templateName, Context context) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom(sender,customName);
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
    }

}
