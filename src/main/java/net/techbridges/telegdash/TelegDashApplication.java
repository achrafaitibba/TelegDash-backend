package net.techbridges.telegdash;

import lombok.AllArgsConstructor;
import net.techbridges.telegdash.service.PlanService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@AllArgsConstructor
public class TelegDashApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(TelegDashApplication.class, args);
    }
    //todo, logging with AOP, create custom annotation
    //todo, make bot auto-join groups via link, if possible, otherwise, auto add it
    //todo, use jackson to map data coming from telegdash.py api
    //todo, check channels infos before downgrading plans

    //todo, lifetime subs for clients, not visitors
    //todo, free accounts like doc, should have a limited access (eg. six months)
    //todo, save emails from subscription details after being subscribed
    //todo, remove plan endpoints, or restrict access
    //todo, members count for plans should ignore kicked accounts !
    //todo, kicked members if joined again should update status to ACTIVE
    //todo, check value type by attribute valueType before update/create value
}
