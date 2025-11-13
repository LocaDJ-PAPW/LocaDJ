package com.LocaDj.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEntity {
    private String id;
    private String status;
    private String orderId;
    private String amount;
    private String statusDetail;
    private Payer payer;
    private String paymentMethodId;
}
