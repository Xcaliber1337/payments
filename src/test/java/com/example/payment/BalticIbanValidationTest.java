package com.example.payment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BalticIbanValidationTest {

    private static Validator validator;

    static class TestObject {
        @BalticIban
        private String debtorIban;

        public void setDebtorIban(String debtorIban) {
            this.debtorIban = debtorIban;
        }
    }

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidBalticIbans() {
        String[] validIbans = {
                "LT121000011101001000",
                "LV80BANK0000435195001",
                "EE382200221020145685"
        };

        for (String iban : validIbans) {
            TestObject obj = new TestObject();
            obj.setDebtorIban(iban);

            Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
            assertTrue(violations.isEmpty(), "Expected no violations for IBAN: " + iban);
        }
    }

    @Test
    void testInvalidBalticIbans() {
        String[] invalidIbans = {
                "GB82WEST12345698765432", // Non-Baltic country
                "LT12100001110",         // Too short
                "LV80BANK0000435195001234567890123456" // Too long
        };

        for (String iban : invalidIbans) {
            TestObject obj = new TestObject();
            obj.setDebtorIban(iban);

            Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
            assertFalse(violations.isEmpty(), "Expected violations for IBAN: " + iban);
        }
    }
}
