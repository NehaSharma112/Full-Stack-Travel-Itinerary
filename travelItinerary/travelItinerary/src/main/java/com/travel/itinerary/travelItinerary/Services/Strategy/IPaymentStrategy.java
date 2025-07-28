package com.travel.itinerary.travelItinerary.Services.Strategy;

import com.travel.itinerary.travelItinerary.Dto.PaymentRequest;
import com.travel.itinerary.travelItinerary.Dto.PaymentResponse;

public interface IPaymentStrategy {
    PaymentResponse processPayment(PaymentRequest paymentRequest);
}
