package com.travel.itinerary.travelItinerary.Services.Strategy;

import com.travel.itinerary.travelItinerary.Dto.PaymentRequest;
import com.travel.itinerary.travelItinerary.Dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CryptoPayments implements IPaymentStrategy{

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest, String userName) {
//        log.info();
//        return PaymentResponse.;
        return null;
    }
}
