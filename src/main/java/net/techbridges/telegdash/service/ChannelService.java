package net.techbridges.telegdash.service;

import lombok.AllArgsConstructor;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.telegdashTelethonClientGateway.controller.TelegDashPyApiController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChannelService {
    private final TelegDashPyApiController telegDashPyApiController;

    public Integer checkAdminStatus(String channelId){
        try{
            return telegDashPyApiController.checkAdminStatus(channelId);
        }catch (Exception e){
            throw new RequestException("Something is wrong with the provided ID, details : "  + e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
