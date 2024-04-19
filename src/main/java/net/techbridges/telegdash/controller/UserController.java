package net.techbridges.telegdash.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.dto.request.UserAuthRequest;
import net.techbridges.telegdash.dto.response.UserAuthResponse;
import net.techbridges.telegdash.model.User;
import net.techbridges.telegdash.service.UserService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;


    @PostMapping("/register")
    public UserAuthResponse register(@RequestBody UserAuthRequest user){
        return userService.register(user);
    }

    @PostMapping("/authenticate")
    public UserAuthResponse authenticate(@RequestBody UserAuthRequest user){
        return userService.authenticate(user);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    )throws Exception{
        userService.refreshToken(request, response);
    }

    @GetMapping("/test")

    public String test(){
        return "You good hh";
    }
}
