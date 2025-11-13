package com.LocaDj.services;

import com.LocaDj.client.MercadoPagoClient;
import com.LocaDj.DTOs.ProccessNotificationResponseDTO;
import com.LocaDj.models.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ProccessPaymentNotificationService {

    private final MercadoPagoClient mercadoPagoClient;

    public ProccessNotificationResponseDTO proccessPaymentNotification(String id, String type) {

        log.info("Iniciando notificação de pagamento com id: {} e tipo: {}", id, type);

        try {
            PaymentEntity payment = mercadoPagoClient.getPaymentoStatus(Long.valueOf(id));

            return new ProccessNotificationResponseDTO(true, payment.getStatus());
        }
        catch(Exception e){
            log.info("Error processing notification for id {}: {}", id, e.getMessage());
            return new ProccessNotificationResponseDTO(false, "SERVER_ERROR");
        }

    }

}
