package com.LocaDj.controller;


import com.LocaDj.DTOs.CreateResponseDTO;
import com.LocaDj.DTOs.CreatePreferenceRequestDTO;
import com.LocaDj.DTOs.PaymentRequestDto;
import com.LocaDj.models.Payer;
import com.LocaDj.models.Reservation;
import com.LocaDj.models.User;
import com.LocaDj.services.CreatePaymentPreferenceService;
import com.LocaDj.services.ReservationService;
import com.LocaDj.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final CreatePaymentPreferenceService createPaymentPreferenceRequestDTO;
    private final UserService userService;
    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<CreateResponseDTO> createPreference(@Valid @RequestBody PaymentRequestDto paymentRequestDto, Principal principal){
        try{
            String userName = principal.getName();
            User loggedUser = userService.findByEmail(userName)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            long userId = loggedUser.getId();

            Reservation reservation = reservationService.findById(paymentRequestDto.getReservationId())
                    .orElseThrow(() -> new UsernameNotFoundException("Reserva não encontrada!"));

            CreatePreferenceRequestDTO.PayerDTO payer = new CreatePreferenceRequestDTO.PayerDTO(loggedUser.getEmail(), loggedUser.getName() );

            CreatePreferenceRequestDTO.BackUrlsDTO backUrlsDTO = new CreatePreferenceRequestDTO.BackUrlsDTO("/checkout/success","/checkout/failure", "/checkout/pending" );

            CreatePreferenceRequestDTO.ItemDTO item = new CreatePreferenceRequestDTO.ItemDTO(reservation.getKit().getId().toString(),reservation.getKit().getName(), reservation.getDaily(), BigDecimal.valueOf(reservation.getKit().getPricePerDay()) );

            CreatePreferenceRequestDTO.SimpleAddressDTO address = new CreatePreferenceRequestDTO.SimpleAddressDTO("Washington Soares", "150", "60811341");
            List<CreatePreferenceRequestDTO.ItemDTO> itens = Collections.singletonList(item);
            CreatePreferenceRequestDTO request = CreatePreferenceRequestDTO.builder().
                    userId(userId)
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

        }catch(Exception e){
            log.info("Error creating payment preference {}:", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }
}
