package com.LocaDj.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private Long reservationId;
    private String successUrl;
    private String failureUrl;
    private String pendingUrl;
}
