package com.example.payment.service;

import com.example.payment.Payment;
import com.example.payment.dto.PaymentRequestDTO;
import com.example.payment.repository.PaymentRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class PaymentControllerIntegrationTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testCreatePayment() {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setAmount(new BigDecimal("100.00"));
        requestDTO.setDebtorIban("LT121000011101001000");

        String clientCountry = "LT";

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(requestDTO.getAmount());
        payment.setDebtorIban(requestDTO.getDebtorIban());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setClientCountry(clientCountry);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.createPayment(requestDTO, clientCountry);

        assertNotNull(result);
        assertEquals(requestDTO.getAmount(), result.getAmount());
        assertEquals(requestDTO.getDebtorIban(), result.getDebtorIban());
        assertEquals(clientCountry, result.getClientCountry());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
     void testGetPaymentsWithDebtorIban() {
        String debtorIban = "LT121000011101001000";
        List<Payment> payments = Arrays.asList(new Payment(), new Payment());

        when(paymentRepository.findByDebtorIban(debtorIban)).thenReturn(payments);

        List<Payment> result = paymentService.getPayments(debtorIban);

        assertEquals(2, result.size());
        verify(paymentRepository, times(1)).findByDebtorIban(debtorIban);
    }

    @Test
     void testGetPaymentsWithoutDebtorIban() {
        List<Payment> payments = Arrays.asList(new Payment(), new Payment(), new Payment());

        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> result = paymentService.getPayments(null);

        assertEquals(3, result.size());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
     void testProcessPaymentsFromCsvValidData() throws Exception {
        String csvContent = "amount,debtorIban\n" +
                "100.00,LT121000011101001000\n" +
                "200.00,LV80BANK0000435195001";

        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        when(validator.validate(any(PaymentRequestDTO.class))).thenReturn(Collections.emptySet());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String clientCountry = "LT";
        List<Payment> payments = paymentService.processPaymentsFromCsv(file, clientCountry);

        assertEquals(2, payments.size());
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
     void testProcessPaymentsFromCsvInvalidData() throws Exception {
        String csvContent = "amount,debtorIban\n" +
                "-100.00,InvalidIBAN\n" +
                "abc,EE382200221020145685";

        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        Set<ConstraintViolation<PaymentRequestDTO>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));

        when(validator.validate(any(PaymentRequestDTO.class))).thenReturn(violations);

        String clientCountry = "LT";
        List<Payment> payments = paymentService.processPaymentsFromCsv(file, clientCountry);

        assertEquals(0, payments.size());
        verify(paymentRepository, times(0)).save(any(Payment.class));
    }
}