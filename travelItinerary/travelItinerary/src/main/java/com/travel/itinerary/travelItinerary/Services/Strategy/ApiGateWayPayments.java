package com.travel.itinerary.travelItinerary.Services.Strategy;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.travel.itinerary.travelItinerary.Dto.PaymentRequest;
import com.travel.itinerary.travelItinerary.Dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ApiGateWayPayments implements IPaymentStrategy{
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
//  key= sk_test_51Rmi1z4EgsjNbTIPFsycnTjFLoq7QVOgPjJHc4l5iLF12AfLUaId8nONWFIyAG58w09e3u2Y7jZgBGtZHCeLFdUx00KZGhnQeq



    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest, String userName) {
        try {
            log.info("Stripe secret key loaded: {}", stripeSecretKey != null ? "****" + stripeSecretKey.substring(stripeSecretKey.length() - 4) : "NULL");
            if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty()) {
                log.error("Stripe secret key is null or empty");
                return PaymentResponse.failed("Stripe API key not configured");
            }

            Stripe.apiKey = stripeSecretKey;

            long amountInCents = paymentRequest.getAmount().multiply(new BigDecimal("100")).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(paymentRequest.getCurrency().toLowerCase())
                    .setPaymentMethod(paymentRequest.getPaymentMethodId())
                    .setDescription(paymentRequest.getDescription())
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                    .setConfirm(true)
                    .setReturnUrl("https://website/return")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            log.info("Payment processed successfully. Payment Intent ID: {}", paymentIntent.getId());

            //savePayment(paymentRequest, userName);

            return PaymentResponse.builder()
                    .paymentId(paymentIntent.getId())
                    .status(paymentIntent.getStatus())
                    .amount(paymentRequest.getAmount())
                    .currency(paymentRequest.getCurrency())
                    .paymentMethod("stripe_gateway")
                    .description(paymentRequest.getDescription())
                    .clientSecret(paymentIntent.getClientSecret())
                    .success("succeeded".equals(paymentIntent.getStatus()) || "requires_action".equals(paymentIntent.getStatus()))
                    .build();

        } catch (StripeException e) {
            log.error("Stripe payment failed: {}", e.getMessage());
            return PaymentResponse.failed("Payment failed: " + e.getUserMessage());
        } catch (Exception e) {
            log.error("Payment processing failed: {}", e.getMessage());
            return PaymentResponse.failed("Payment processing failed: " + e.getMessage());
        }
    }

}
