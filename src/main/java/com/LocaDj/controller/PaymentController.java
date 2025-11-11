package com.LocaDj.controller;


import com.LocaDj.DTOs.CreateResponseDTO;
import com.LocaDj.DTOs.CreatePreferenceRequestDTO;
import com.LocaDj.services.CreatePaymentPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final CreatePaymentPreferenceService createPaymentPreferenceRequestDTO;

    @PostMapping()
    public ResponseEntity<CreateResponseDTO> createPreference(@Valid @RequestBody CreatePreferenceRequestDTO request){
        try{

            CreateResponseDTO response = createPaymentPreferenceRequestDTO.createPrefernce(request);

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
