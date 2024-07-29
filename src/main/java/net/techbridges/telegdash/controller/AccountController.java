package net.techbridges.telegdash.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.dto.request.AccountAuthRequest;
import net.techbridges.telegdash.dto.request.AccountRegisterRequest;
import net.techbridges.telegdash.dto.response.AccountAuthResponse;
import net.techbridges.telegdash.dto.response.AccountRegisterResponse;
import net.techbridges.telegdash.model.Feedback;
import net.techbridges.telegdash.service.AccountService;
import net.techbridges.telegdash.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;


@RestController
@RequestMapping("/api/v1.0/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final EmailService emailService;


    @PostMapping("/register")
    public ResponseEntity<AccountRegisterResponse> register(@RequestBody AccountRegisterRequest account) throws Exception{
        return ResponseEntity.ok().body(accountService.register(account));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AccountAuthResponse> authenticate(@RequestBody AccountAuthRequest account){
        return ResponseEntity.ok().body(accountService.authenticate(account));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    )throws Exception{
        accountService.refreshToken(request, response);
    }

    @GetMapping("/recover-password-url/{email}")
    public String passwordResetUrl(@PathVariable String email, Model model) throws Exception {
        Context context = new Context();
        String passwordResetUrl = emailService.passwordResetUrl(email, "recover-password", context);
        model.addAttribute("passwordResetUrl", passwordResetUrl);
        return "recover-password";
    }


    @PostMapping("/reset-password")
    public ResponseEntity<AccountAuthResponse> recoverPassword(@RequestBody AccountAuthRequest request) {
        return ResponseEntity.ok().body(accountService.recoverPassword(request.email(), request.password()));
    }

    @PostMapping("/feedback")
    public ResponseEntity<Feedback> feedback(@RequestBody Feedback request) {
        return ResponseEntity.ok().body(accountService.saveFeedback(request));
    }

    @GetMapping("/test")
    public String test(){
        return "You good hh";
    }
}
