package net.techbridges.telegdash.paymentService.paypal.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @JsonProperty("id")
    private String id;
    @JsonProperty("status")
    private SubscriptionStatus status;
    @JsonProperty("status_update_time")
    private String statusUpdateTime;
    @JsonProperty("plan_id")
    private String planId;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("subscriber")
    private Subscriber subscriber;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("links")
    private List<Link> links ;


}