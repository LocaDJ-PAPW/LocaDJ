package com.LocaDj.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;


import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    @GetMapping()
    public String showCheckoutPage() {
        return "checkout/checkout";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "checkout/success"; // Retorna a página de sucesso
    }
}
