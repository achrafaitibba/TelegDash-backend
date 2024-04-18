package net.techbridges.telegdash.mailingService;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

@RestController
@AllArgsConstructor
@RequestMapping("/emails")
public class EmailController {

    private EmailServiceImpl emailService;


    @PostMapping("/send-html-email")
    public String sendHtmlEmail(@RequestBody EmailDetails emailRequest) throws Exception{
        Context context = new Context();
        //context.setVariable("message", emailRequest.getMsgBody());
        emailService.sendEmailWithHtmlTemplate(emailRequest.getRecipient(), emailRequest.getSubject(), "email-template", context);
        return "HTML email sent successfully!";
    }
}
