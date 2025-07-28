package com.travel.itinerary.travelItinerary.Services.Strategy;

import com.travel.itinerary.travelItinerary.Dto.PaymentRequest;
import com.travel.itinerary.travelItinerary.Dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApiGateWayPayments implements IPaymentStrategy{
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
//  key= sk_test_51Rmi1z4EgsjNbTIPFsycnTjFLoq7QVOgPjJHc4l5iLF12AfLUaId8nONWFIyAG58w09e3u2Y7jZgBGtZHCeLFdUx00KZGhnQeq
    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        return null;
    }
}
