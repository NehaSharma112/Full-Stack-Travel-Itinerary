package com.travel.itinerary.travelItinerary.Services;

import com.travel.itinerary.travelItinerary.Dto.PaymentRequest;
import com.travel.itinerary.travelItinerary.Dto.PaymentResponse;
import com.travel.itinerary.travelItinerary.Services.Strategy.IPaymentStrategy;
import com.travel.itinerary.travelItinerary.Services.Strategy.PaymentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentFactory paymentFactory;

    public PaymentResponse processPayment(PaymentRequest paymentRequest, String userName){
        try{
            //if paymentrequest is null its error
            if(paymentRequest == null){
                log.error("Payment request is null");
                return PaymentResponse.failed("Payment Request cannot be null");
            }

            //amount shouldn't be null and negative
            if(paymentRequest.getAmount()==null || paymentRequest.getAmount().compareTo(BigDecimal.ZERO) <=0){
                log.error("Invalid payment amount: {}", paymentRequest.getAmount());
                return PaymentResponse.failed("Payment amount must be greater than zero");
            }
            IPaymentStrategy paymentStrategy = paymentFactory.getInstance(paymentRequest.getPaymentType());

            //if payment type is not provided by us
            if (paymentStrategy == null) {
                log.error("Unsupported payment type: {}", paymentRequest.getPaymentType());
                return PaymentResponse.failed("Unsupported payment type: " + paymentRequest.getPaymentType());
            }

            log.info("Processing payment of {} {} using {} strategy",
                    paymentRequest.getAmount(),
                    paymentRequest.getCurrency(),
                    paymentRequest.getPaymentType());

            PaymentResponse paymentResponse = paymentStrategy.processPayment(paymentRequest,userName);

            log.info("Payment processing completed. Success: {}, Payment ID: {}",
                    paymentResponse.isSuccess(), paymentResponse.getPaymentId());

            return paymentResponse;

        }catch(Exception e){
            log.error("Unexpected error during payment processing: {}", e.getMessage(), e);
            return PaymentResponse.failed("Payment processing failed due to unexpected error");
        }
    }
}
