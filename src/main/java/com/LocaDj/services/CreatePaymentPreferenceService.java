package com.LocaDj.services;

import com.LocaDj.client.MercadoPagoClient;
import com.LocaDj.DTOs.CreatePreferenceRequestDTO;
import com.LocaDj.DTOs.CreateResponseDTO;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CreatePaymentPreferenceService {
    private final MercadoPagoClient mercadoPagoClient;



    public CreateResponseDTO createPrefernce(CreatePreferenceRequestDTO inputData, String orderNumber){
        log.info("Creating payment preference with request: {}", inputData);



        try{
            return mercadoPagoClient.createPreference(inputData, orderNumber);

        }
        catch (MPException e){
            log.info("Erro ao criar preferência de pagamento: {}", e.getMessage());
            throw  new RuntimeException(e);
        }
        catch (MPApiException e){
            log.info("Erro ao criar pagamento: {}", e.getMessage());
            throw  new RuntimeException(e);
        }
    }
}
