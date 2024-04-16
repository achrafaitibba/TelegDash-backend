package net.techbridges.telegdash.telegdashTelethonClientGateway.model;


public record BasicMember
        (String telegramId,
         String username,
         String firstName,
         String lastName) {
}
