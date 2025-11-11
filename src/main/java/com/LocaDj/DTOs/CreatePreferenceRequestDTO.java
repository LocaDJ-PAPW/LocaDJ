package com.LocaDj.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;


@Builder
public record CreatePreferenceRequestDTO(
        Long userId,

        @DecimalMin(value = "0.01", message = "Total amount must be greater than zero")
        BigDecimal totalAmount,

        PayerDTO payer,

        @NotNull(message = "Back URLs cannot be null")
        @Valid
        @JsonProperty("back_urls")
        BackUrlsDTO backUrls,

        SimpleAddressDTO deliveryAddress,

        @JsonProperty("auto_return")
        String autoReturn,

        @NotEmpty(message = "Items list cannot be empty")
        @Valid
        List<ItemDTO> items
) {



    public record  PayerDTO(
            String name,
            String email
    ){}

    public record  BackUrlsDTO(
            @JsonProperty("success")
            String success,
            @JsonProperty("failure")
            String failure,
            @JsonProperty("pending")
            String pending
    ){}

    // Este é apenas um exemplo baseado na documentação do Mercado Pago
    public record SimpleAddressDTO(
            String street_name,
            String street_number,
            String zip_code
    ) {}

    public record  ItemDTO(
            String Id,
            String title,
            Integer days,
            BigDecimal unitPrice
    ){}
}
