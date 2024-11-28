package com.example.payment.controller;

import com.example.payment.CountryResolver;
import com.example.payment.Payment;
import com.example.payment.dto.PaymentRequestDTO;
import com.example.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @Valid @RequestBody PaymentRequestDTO requestDTO,
            HttpServletRequest request) {

        String clientIp = request.getHeader("X-Forwarded-For");
        String clientCountry = CountryResolver.resolveCountry(clientIp);

        Payment payment = paymentService.createPayment(requestDTO, clientCountry);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @PostMapping("/payment-files")
    public ResponseEntity<List<Payment>> uploadPaymentsFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        String clientIp = request.getHeader("X-Forwarded-For");
        String clientCountry = CountryResolver.resolveCountry(clientIp);

        List<Payment> payments = paymentService.processPaymentsFromCsv(file, clientCountry);
        return new ResponseEntity<>(payments, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getPayments(
            @RequestParam(required = false) String debtorIban) {

        List<Payment> payments = paymentService.getPayments(debtorIban);
        return ResponseEntity.ok(payments);
    }
}
