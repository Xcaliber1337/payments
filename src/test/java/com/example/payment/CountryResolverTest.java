package com.example.payment;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
 class CountryResolverTest {

    @Test
     void testResolveCountrySuccess() throws Exception {
        String ipAddress = "8.8.8.8";
        String jsonResponse = "{\"countryCode\":\"US\"}";

        RestTemplate restTemplateMock = mock(RestTemplate.class);
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);

        when(responseEntity.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(responseEntity.getBody()).thenReturn(jsonResponse);
        when(restTemplateMock.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
                })) {

            String country = CountryResolver.resolveCountry(ipAddress);
            assertEquals("US", country);
        }
    }

    @Test
     void testResolveCountryFailure() throws Exception {
        String ipAddress = "invalid_ip";

        RestTemplate restTemplateMock = mock(RestTemplate.class);
        when(restTemplateMock.getForEntity(anyString(), eq(String.class))).thenThrow(new RuntimeException("Invalid IP"));

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.getForEntity(anyString(), eq(String.class))).thenThrow(new RuntimeException("Invalid IP"));
                })) {

            String country = CountryResolver.resolveCountry(ipAddress);
            assertNull(country);
        }
    }
}
