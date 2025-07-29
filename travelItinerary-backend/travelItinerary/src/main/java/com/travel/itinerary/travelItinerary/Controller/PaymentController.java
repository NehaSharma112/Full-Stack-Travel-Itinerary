package com.travel.itinerary.travelItinerary.Controller;

import com.travel.itinerary.travelItinerary.Model.User;
import com.travel.itinerary.travelItinerary.Repository.UserRepository;
import com.travel.itinerary.travelItinerary.Services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/payment")
@Slf4j//for logger
@RequiredArgsConstructor
public class PaymentController {
    @Value("${stripe.secret.key}")
    private String stripePublicKey;

    private final UserRepository userRepository;
    private final PaymentService paymentService;

    private String getAuthenticatedUsername(HttpServletRequest request) {
        return (String) request.getAttribute("authenticatedUsername");
    }

    private boolean validateUserAccess(String tokenUsername, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return tokenUsername.equals(user.getUsername());
    }
}
