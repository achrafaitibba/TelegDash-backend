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
    //todo, automate the payments via patreon


    //todo, upgrade to premium/annually after checking current plan, also make price lower 'upgrade to premium with $100..'
    //todo, handle error of external apis by checking their status and returning their error message "400 Bad Request: "{"error":"The provided phone number is invalid."}"] with root cause..."
    //todo, hide plan/mailing controller endpoints
    //todo, check token owner 'extract them' before updating password
    //todo, cors origin for python api
    //todo, add lifetime subscription type for members
    //todo, save emails from subscription details after being subscribed
    //todo, refresh token mechanism
    //todo, limit origins to our website only
    //todo, test embeddable plans

    //todo, send register email after registration
    //todo, lifetime subs for clients, not visitors
    //todo, free accounts like doc, should have a limited access (eg. six months)
    //todo, confirm email endpoint
    //todo, store sessions somewhere as a backup
    //todo, email verification
}
