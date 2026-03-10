package com.springAidemo;

import java.time.LocalDateTime;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GetInfo {

    private final RestClient restClient = RestClient.create();

    @Tool(description = "Get current date and time")
    public String getCurrentDateTime() {
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
    }

    @Tool(description = "Get current weather for any city by name")
    public String getWeather(String city) {
        // Step 1: City name → coordinates (Open-Meteo Geocoding, no API key)
        String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" 
                        + city + "&count=1&language=en&format=json";
System.err.println("geoUrl "+geoUrl);
        String geoResponse = restClient.get()
                .uri(geoUrl)
                .retrieve()
                .body(String.class);

        // Parse lat/lon from response
        double lat = extractDouble(geoResponse, "latitude");
        double lon = extractDouble(geoResponse, "longitude");

        if (lat == 0 && lon == 0) {
            return "City '" + city + "' not found.";
        }

        // Step 2: Coordinates → weather (Open-Meteo Weather, no API key)
        String weatherUrl = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + lat
                + "&longitude=" + lon
                + "&current=temperature_2m,relative_humidity_2m,"
                + "wind_speed_10m,weather_code,rain";

        String weatherResponse = restClient.get()
                .uri(weatherUrl)
                .retrieve()
                .body(String.class);

        return "City: " + city + "\n" + weatherResponse;
    }

    // Simple JSON value extractor (no extra dependency needed)
    private double extractDouble(String json, String key) {
        try {
            String search = "\"" + key + "\":";
            int idx = json.indexOf(search);
            if (idx == -1) return 0;
            int start = idx + search.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return Double.parseDouble(json.substring(start, end).trim());
        } catch (Exception e) {
            return 0;
        }
    }
}