package net.techbridges.telegdash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
public class TelegDashApplication{
    public static void main(String[] args) {
        SpringApplication.run(TelegDashApplication.class, args);
    }
    //todo, logging with AOP, create custom annotation
    //todo, make bot auto-join groups via link, if possible, otherwise, auto add it
    //todo, check channels infos before downgrading plans

    //todo, lifetime subs for clients, not visitors
    //todo, free accounts like doc, should have a limited access (eg. six months)
    //todo, save emails from subscription details after being subscribed
    //todo, remove plan endpoints, or restrict access
    //todo, members count for plans should ignore kicked accounts !
    //todo, kicked members if joined again should update status to ACTIVE
    //todo, check value type by attribute valueType before update/create value, get valueType by attributeId, check it on frontend
    //todo, confirm email endpoint
    //todo, backup on aws ? or my server?
    //todo, limit origins to our website only
    //todo, update channel
    //todo, endpoint to set memberStatus ACTIVE for list of member to update their status
    //todo, store sessions somewhere as a backup
    //todo, email verification
    //todo, update email
}
