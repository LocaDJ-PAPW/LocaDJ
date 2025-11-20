package com.LocaDj.client;

import com.LocaDj.DTOs.CreatePreferenceRequestDTO;
import com.LocaDj.DTOs.CreateResponseDTO;
import com.LocaDj.models.Payer;
import com.LocaDj.models.PaymentEntity;
import com.LocaDj.models.Reservation;
import com.LocaDj.services.ReservationService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoClient {

    private final ReservationService reservationService;
    @Value("${api.v1.mercadopago-access-token}")
    private String accessToken;

    @Value("${api.v1.mercadopago-notification-url}")
    private String notificationUrl;

    @PostConstruct
    public void init(){
        MercadoPagoConfig.setAccessToken(accessToken);

        log.info("Iniciando Mercado Pago");
    }

    public CreateResponseDTO createPreference(CreatePreferenceRequestDTO inputData, String orderNumber) throws MPException, MPApiException {
        log.error("Criando preferência de pagamento no Mercado Pago com dados: {}", inputData );
        try{
            PreferenceClient preferenceClient = new PreferenceClient();

            List<PreferenceItemRequest> items = inputData.items().stream()
                    .map(item -> PreferenceItemRequest.builder()
                            .id(item.Id())
                            .title(item.title())
                            .quantity(item.days())
                            .unitPrice(item.unitPrice())
                            .build())
                    .toList();

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .name(inputData.payer().name())
                    .email(inputData.payer().email())
                    .build();

            PreferenceBackUrlsRequest backUrlsRequest = PreferenceBackUrlsRequest.builder()
                    .success(inputData.backUrls().success())
                    .failure(inputData.backUrls().failure())
                    .pending(inputData.backUrls().pending())
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrlsRequest)
                    .externalReference(orderNumber)
                    .autoReturn(inputData.autoReturn().toLowerCase())
                    .build();

            log.info(inputData.backUrls().success());
            Preference preference = preferenceClient.create(preferenceRequest);

            log.info("Preferência criada com sucesso no Mercado Pago: {} e {}", preference.getId(), preference.getInitPoint());


            return new CreateResponseDTO(
                    preference.getId(),
                    preference.getInitPoint()
            );

        }
        catch (MPApiException apiException) {
            // Captura a exceção da API
            log.error("Erro ao criar preferência no Mercado Pago na api: Api error. Check response for details");
            log.error("Status da resposta da API: {}", apiException.getStatusCode());
            log.error("Conteúdo da resposta da API (corpo JSON): {}", apiException.getApiResponse().getContent());

            // Re-lança a exceção para que ela possa ser tratada em outro lugar
            throw apiException;
        }
        catch (MPException e){
            log.error("Erro ao criar preferência no Mercado Pago {}", e.getMessage());
            throw e;
        }
        catch (Exception e){
            log.error("Erro inesperado ao criar preferência no Mercado Pago na api: {}", e.getMessage());
            throw  new MPException("Erro inesperado ao criar preferência", e);
        }


    }

    public PaymentEntity getPaymentoStatus(Long id) throws MPException, MPApiException {
        PaymentClient paymentClient = new PaymentClient();
        Payment paymentMercadoPago = paymentClient.get(id);

        if(paymentMercadoPago == null){
            log.error("Pagamento não encontrado com ID {}: ", id);
            throw new MPException("Pagamento não encontrado");
        }

        String status = paymentMercadoPago.getStatus();
        String paymentMethods = paymentMercadoPago.getPaymentMethodId();

        log.info("Status do paamento: {} e método de pagamento: {}", status, paymentMethods);

        Payer payer = null;

        if(paymentMercadoPago.getPayer() != null && paymentMercadoPago.getPayer().getIdentification() != null){
            payer = Payer.builder()

                    .email(paymentMercadoPago.getPayer().getEmail())
                    .firstName(paymentMercadoPago.getPayer().getFirstName())
                    .lastName(paymentMercadoPago.getPayer().getLastName())
                    .identification(Payer.Identification.builder()
                            .type(paymentMercadoPago.getPayer().getIdentification().getType())
                            .number(paymentMercadoPago.getPayer().getIdentification().getNumber())
                            .build())
                    .build();
        }

        Reservation reservation = reservationService.findById(Long.valueOf(paymentMercadoPago.getExternalReference()))
                .orElseThrow(() -> new UsernameNotFoundException("Reserva não encontrada!"));

        if(paymentMercadoPago.getStatus().equalsIgnoreCase("APPROVED")) {
            reservationService.confirmReservation(reservation.getId());
        }


        return PaymentEntity.builder()
                .id(paymentMercadoPago.getId().toString())
                .orderId(paymentMercadoPago.getExternalReference())
                .status(status)
                .amount(paymentMercadoPago.getTransactionAmount() != null ? paymentMercadoPago.getTransactionAmount().toString() : null)
                .statusDetail(paymentMercadoPago.getStatusDetail())
                .payer(payer)
                .paymentMethodId(paymentMethods)
                .build();


    }
}