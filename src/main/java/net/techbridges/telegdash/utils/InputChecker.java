package net.techbridges.telegdash.utils;

import net.techbridges.telegdash.model.GroupType;

public class InputChecker {

    public static String normalizeEmail(String email) {
        int atIndex = email.indexOf('@');
        int plusIndex = email.indexOf('+');
        if (plusIndex != -1 && plusIndex < atIndex) {
            return email.substring(0, plusIndex) + email.substring(atIndex);
        } else {
            return email;
        }
    }

    /**
     * Type of username >   ID           >  converted ID
     * Private group    >   -1234343454  >  -1234343454
     * Private channel  >   -1234343454  >  (-100)1234343454
     * Public group     >   @achrafGroup >  @achrafGroup
     * Public channel   >   @achrafChannel >  @achrafChannel
     *
     *
     * Basically private channels in telegram doesn't allow bot to join,
     * And there is a quick hack to make it work and ignore the restriction,
     * By simply adding (-100) at the beginning of the private channel ID as mentioned in types above
     *
     *
     * public enum GroupType {
     *     PRIVATE_GROUP,
     *     PRIVATE_CHANNEL,
     *     PUBLIC_GROUP,
     *     PUBLIC_CHANNEL
     * }
     */
    public static String channelUsernameBuilder(GroupType type, String username) {

        return switch (type) {
            case PRIVATE_CHANNEL -> "-100" + username.replace("-","");
            case PUBLIC_GROUP, PUBLIC_CHANNEL, PRIVATE_GROUP -> username;
            default -> throw new IllegalArgumentException("Invalid GroupType: " + type);
        };

    }


}
