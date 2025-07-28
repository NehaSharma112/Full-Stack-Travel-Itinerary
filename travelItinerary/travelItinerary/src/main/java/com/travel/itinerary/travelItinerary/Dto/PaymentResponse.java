package com.travel.itinerary.travelItinerary.Dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {


        private String paymentId; // For Stripe payment method ID
        private BigDecimal amount;
        private String currency;
        private String paymentMethod; // "gateway" or "crypto"
        private String description;
        private String status;
        private LocalDateTime createdAt;
        private String clientSecret; // For Stripe frontend confirmation
        private String receiptUrl;
        private boolean success;
        private String errorMessage;

        // Factory methods for easier creation
        public static PaymentResponse success(String paymentId, BigDecimal amount, String currency, String clientSecret){
            return PaymentResponse.builder()
                    .paymentId(paymentId)
                    .status("Succeeded")
                    .amount(amount)
                    .currency(currency)
                    .clientSecret(clientSecret)
                    .createdAt(LocalDateTime.now())
                    .success(true)
                    .build();
        }

        public static PaymentResponse failed(String errorMessage){
            return PaymentResponse.builder()
                    .status("Failed")
                    .errorMessage(errorMessage)
                    .success(false)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
}



