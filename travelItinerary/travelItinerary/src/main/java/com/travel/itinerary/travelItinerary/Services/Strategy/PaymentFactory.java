package com.travel.itinerary.travelItinerary.Services.Strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFactory {
    public static IPaymentStrategy getInstance(String type){
        return switch (type) {
            case "gateway" -> new ApiGateWayPayments();
            case "crypto" -> new CryptoPayments();
            default -> null;
        };
    }
}
