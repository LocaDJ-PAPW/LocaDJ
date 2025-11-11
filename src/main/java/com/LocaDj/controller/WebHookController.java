package com.LocaDj.controller;

import com.LocaDj.DTOs.MercadoPagoConfigDTO;
import com.LocaDj.services.ProccessPaymentNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebHookController {

    private final ProccessPaymentNotificationService proccessPaymentNotificationService;


    @PostMapping("/mercadopago")
    public ResponseEntity<Void> handlerNotification(@RequestBody MercadoPagoConfigDTO mercadoPagoConfigDTO){

        log.info("Webhook recebido: {}", mercadoPagoConfigDTO);
        String resourceId = mercadoPagoConfigDTO.getData().getId();
        String resourceType= mercadoPagoConfigDTO.getType();

        try{
            var result = proccessPaymentNotificationService.proccessPaymentNotification(resourceId, resourceType);

            log.info("Webhook processed successfully for resource ID: {} an type {}", result.isSuccess(), result.getUpdateStatus());

        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}