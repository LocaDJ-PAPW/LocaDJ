package com.LocaDj.controller.api;

import com.LocaDj.DTOs.CreatePreferenceRequestDTO;
import com.LocaDj.DTOs.CreateResponseDTO;
import com.LocaDj.DTOs.PaymentRequestDto;
import com.LocaDj.client.MercadoPagoClient;
import com.LocaDj.models.PaymentEntity;
import com.LocaDj.models.Reservation;
import com.LocaDj.models.User;
import com.LocaDj.services.CreatePaymentPreferenceService;
import com.LocaDj.services.ReservationService;
import com.LocaDj.services.UserService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutApiController {

    private final MercadoPagoClient client;
    private final CreatePaymentPreferenceService createPaymentPreferenceRequestDTO;
    private final UserService userService;
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<CreateResponseDTO> createPreference(@Valid @RequestBody PaymentRequestDto paymentRequestDto, Principal principal){
        try{
            String userName = principal.getName();
            User loggedUser = userService.findByEmail(userName)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            long userId = loggedUser.getId();

            Reservation reservation = reservationService.findById(paymentRequestDto.getReservationId())
                    .orElseThrow(() -> new UsernameNotFoundException("Reserva não encontrada!"));

            CreatePreferenceRequestDTO.PayerDTO payer = new CreatePreferenceRequestDTO.PayerDTO(loggedUser.getName(), loggedUser.getEmail());
            String baseUrl = "https://locadj.onrender.com";

            CreatePreferenceRequestDTO.BackUrlsDTO backUrlsDTO = new CreatePreferenceRequestDTO.BackUrlsDTO(
                    paymentRequestDto.getSuccessUrl(), // A URL gerada no Expo do usuario de sucesso!
                    paymentRequestDto.getFailureUrl(), // A do expo de falha!
                    paymentRequestDto.getPendingUrl()  // A do expo pendente!
            );

            CreatePreferenceRequestDTO.ItemDTO item = new CreatePreferenceRequestDTO.ItemDTO(
                    reservation.getKit().getId().toString(),
                    reservation.getKit().getName(),
                    reservation.getDaily(),
                    BigDecimal.valueOf(reservation.getKit().getPricePerDay())
            );

            // Dica: Se futuramente você for enviar o endereço do React Native, poderá substituir os dados fixos abaixo
            CreatePreferenceRequestDTO.SimpleAddressDTO address = new CreatePreferenceRequestDTO.SimpleAddressDTO("Washington Soares", "150", "60811341");
            List<CreatePreferenceRequestDTO.ItemDTO> itens = Collections.singletonList(item);

            CreatePreferenceRequestDTO request = CreatePreferenceRequestDTO.builder()
                    .userId(userId)
                    .payer(payer)
                    .backUrls(backUrlsDTO)
                    .items(itens)
                    .totalAmount(BigDecimal.valueOf(reservation.getTotalAmount()))
                    .deliveryAddress(address)
                    .autoReturn("approved")
                    .build();

            CreateResponseDTO response = createPaymentPreferenceRequestDTO.createPrefernce(request, reservation.getId().toString());

            return ResponseEntity.ok(new CreateResponseDTO(
                    response.prefrenceId(),
                    response.redirectUrl()
            ));

        } catch(Exception e){
            log.error("Error creating payment preference: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

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
