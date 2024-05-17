package net.techbridges.telegdash.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.dto.request.AccountAuthRequest;
import net.techbridges.telegdash.dto.request.AccountRegisterRequest;
import net.techbridges.telegdash.dto.response.AccountAuthResponse;
import net.techbridges.telegdash.dto.response.AccountRegisterResponse;
import net.techbridges.telegdash.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1.0/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;


    @PostMapping("/register")
    public ResponseEntity<AccountRegisterResponse> register(@RequestBody AccountRegisterRequest account){
        return ResponseEntity.ok().body(accountService.register(account));
    }

    @PostMapping("/authenticate")
    public AccountAuthResponse authenticate(@RequestBody AccountAuthRequest account){
        return accountService.authenticate(account);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    )throws Exception{
        accountService.refreshToken(request, response);
    }

    @GetMapping("/test")
    public String test(){
        return "You good hh";
    }
}
