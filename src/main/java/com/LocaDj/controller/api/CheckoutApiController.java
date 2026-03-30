package com.LocaDj.controller.api;

import com.LocaDj.client.MercadoPagoClient;
import com.LocaDj.models.PaymentEntity;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutApiController {

    private final MercadoPagoClient client;


    @GetMapping("/status/{paymentId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String paymentId) {
        try {
            PaymentEntity payment = client.getPaymentoStatus(Long.valueOf(paymentId));

            Map<String, Object> response = new HashMap<>();
            response.put("reservationId", payment.getOrderId());
            response.put("status", payment.getStatus()); // Ex: approved, rejected
            response.put("paymentDate", payment.getDateApproved());
            response.put("amount", payment.getAmount());
            response.put("paymentMethod", payment.getPaymentMethodId());

            return ResponseEntity.ok(response);

        } catch (MPApiException | MPException | NumberFormatException e) {
            log.error("Erro ao buscar status do pagamento: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Erro ao processar informação de pagamento.");
        }
    }


    @GetMapping("/reservation/{id}")
    public ResponseEntity<Map<String, Long>> getCheckoutInfo(@PathVariable long id) {
        Map<String, Long> response = new HashMap<>();
        response.put("reservationId", id);
        return ResponseEntity.ok(response);
    }
}
