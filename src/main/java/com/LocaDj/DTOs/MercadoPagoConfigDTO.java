package com.LocaDj.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MercadoPagoConfigDTO {
    private String action;

    @JsonProperty("api_version")
    private String api_version;
    private Data data;

    @JsonProperty("date_created")
    private String date_created;
    private Long id;

    @JsonProperty("live_mode")
    private boolean live_node;
    private String type;

    @JsonProperty("user_id")
    private Long user_id;


    @lombok.Data
    @NoArgsConstructor
    public static class Data{
        private String id;
    }

}