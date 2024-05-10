package net.techbridges.telegdash.paymentService.paypal.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Subscription {
    @JsonProperty("status")
    private String status;
    @JsonProperty("status_update_time")
    private String statusUpdateTime;
    @JsonProperty("id")
    private String id;
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