package net.techbridges.telegdash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class TelegDashApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(TelegDashApplication.class, args);
    }
    //todo, logging
    //todo, limit max members, no more than 20k for example for each account
    //todo, make bot auto-join groups via link, if possible, other wise, auto add it
    //todo, channel username parser
    //todo, check if bot joined a group/channel
    //todo, use jackson to map data coming from telegdash.py api
    //todo, check channels infos before downgrading plans
}
