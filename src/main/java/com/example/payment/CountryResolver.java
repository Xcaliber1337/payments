package com.example.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CountryResolver {

    private static final Logger logger = LoggerFactory.getLogger(CountryResolver.class);

    public static String resolveCountry(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return null;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://ip-api.com/json/" + ipAddress;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return parseCountryCode(response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error resolving country for IP {}: {}", ipAddress, e.getMessage());
        }
        return null;
    }

    private static String parseCountryCode(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode.get("countryCode").asText();
        } catch (Exception e) {
            logger.error("Error parsing country code from response: {}", e.getMessage());
        }
        return null;
    }
}
