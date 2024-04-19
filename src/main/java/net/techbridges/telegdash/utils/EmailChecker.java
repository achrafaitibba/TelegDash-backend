package net.techbridges.telegdash.utils;

public class EmailChecker {

    public static String normalizeEmail(String email) {
        int atIndex = email.indexOf('@');
        int plusIndex = email.indexOf('+');
        if (plusIndex != -1 && plusIndex < atIndex) {
            return email.substring(0, plusIndex) + email.substring(atIndex);
        } else {
            return email;
        }
    }

}
