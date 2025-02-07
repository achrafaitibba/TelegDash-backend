package net.techbridges.telegdash.controller;


import lombok.AllArgsConstructor;
import net.techbridges.telegdash.model.EmailDetails;
import net.techbridges.telegdash.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1.0/emails")
public class MailingController {

    private EmailService emailService;


    @PostMapping("/send-html-email")
    public String sendHtmlEmail(@RequestBody EmailDetails emailRequest) throws Exception{
        Context context = new Context();
        //context.setVariable("message", emailRequest.getMsgBody());
        emailService.sendEmailWithHtmlTemplate(emailRequest.getRecipient(), emailRequest.getSubject(), "email-template", context);
        return "HTML email sent successfully!";
    }
}
