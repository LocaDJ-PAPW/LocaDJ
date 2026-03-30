package com.LocaDj.controller;

import com.LocaDj.client.MercadoPagoClient;
import com.LocaDj.models.PaymentEntity;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {
    private final MercadoPagoClient client;

    @GetMapping("/{id}")
    public String showCheckoutPage(@PathVariable long id, Model model) {
        model.addAttribute("ReservationId", id);
        return "checkout/checkout";
    }

    @GetMapping("/success")
    public String showSuccessPage(@RequestParam("collection_id") String paymentId,
                                  Model model) throws MPException, MPApiException {

        PaymentEntity payment = client.getPaymentoStatus(Long.valueOf(paymentId));


        model.addAttribute("reservationId", "#" + payment.getOrderId());

        model.addAttribute("paymentDate", payment.getDateApproved());

        model.addAttribute("amount", "R$ " + payment.getAmount());

        model.addAttribute("paymentMethod", payment.getPaymentMethodId());

        return "checkout/success";
    }

    @GetMapping("/failure")
    public String showFailurePage() {
        return "checkout/failure";
    }

    @GetMapping("/pending")
    public String showPendingPage() {
        return "checkout/pending";
    }
}
