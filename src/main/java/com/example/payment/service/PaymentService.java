package com.example.payment.service;

import com.example.payment.Payment;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.dto.PaymentRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private Validator validator;

    public Payment createPayment(PaymentRequestDTO request, String clientCountry) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(request.getAmount());
        payment.setDebtorIban(request.getDebtorIban());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setClientCountry(clientCountry);

        return paymentRepository.save(payment);
    }

    public List<Payment> getPayments(String debtorIban) {
        if (debtorIban != null) {
            return paymentRepository.findByDebtorIban(debtorIban);
        }
        return paymentRepository.findAll();
    }

    public List<Payment> processPaymentsFromCsv(MultipartFile file, String clientCountry) {
        List<Payment> payments = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("amount", "debtorIban")
                    .withSkipHeaderRecord()
                    .parse(reader);

            for (CSVRecord record : records) {
                PaymentRequestDTO dto = new PaymentRequestDTO();
                dto.setAmount(new BigDecimal(record.get("amount")));
                dto.setDebtorIban(record.get("debtorIban"));

                Set<ConstraintViolation<PaymentRequestDTO>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    continue;
                }

                Payment payment = createPayment(dto, clientCountry);
                payments.add(payment);
            }
        } catch (Exception e) {
        }

        return payments;
    }
}
