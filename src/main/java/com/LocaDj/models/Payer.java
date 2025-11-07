package com.LocaDj.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payer {

    String email;
    String firstName;
    String lastName;
    Identification identification;

    @Data
    @Builder
    public static class Identification{
        String type;
        String number;
    }
}