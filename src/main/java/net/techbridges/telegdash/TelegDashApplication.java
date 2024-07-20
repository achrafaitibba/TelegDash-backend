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
    //todo, members count for plans should ignore kicked accounts !
    //todo, make bot auto-join groups via link, if possible, otherwise, auto add it
    //todo, check value type by attribute valueType before update/create value, get valueType by attributeId, check it on frontend
    //todo, scheduled admin reminder : sendMessageToAdmin
    //todo, inform admin that reminders has been sent to expired members


    //todo, update email
    //todo, test embeddable plans
    //todo, save emails from subscription details after being subscribed
    //todo, limit origins to our website only
    //todo, refresh token mechanism

    //todo, lifetime subs for clients, not visitors
    //todo, free accounts like doc, should have a limited access (eg. six months)
    //todo, confirm email endpoint
    //todo, store sessions somewhere as a backup
    //todo, email verification
    //todo, fix links in email templates
}
