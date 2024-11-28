package com.example.payment.controller;

import com.example.payment.CountryResolver;
import com.example.payment.Payment;
import com.example.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private CountryResolver countryResolver;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testGetPaymentsNoFilter() throws Exception {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setDebtorIban("LT121000011101001000");

        List<Payment> payments = Arrays.asList(payment);

        when(paymentService.getPayments(null)).thenReturn(payments);

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].debtorIban").value("LT121000011101001000"));
    }

    @Test
    void testGetPaymentsWithDebtorIban() throws Exception {
        String debtorIban = "LT121000011101001000";

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setDebtorIban(debtorIban);

        List<Payment> payments = Arrays.asList(payment);

        when(paymentService.getPayments(debtorIban)).thenReturn(payments);

        mockMvc.perform(get("/payments")
                        .param("debtorIban", debtorIban))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].debtorIban").value(debtorIban));
    }
}