package com.travel.itinerary.travelItinerary.Controller;

import com.travel.itinerary.travelItinerary.Dto.ApiResponse;
import com.travel.itinerary.travelItinerary.Dto.AuthResponse;
import com.travel.itinerary.travelItinerary.Dto.EmailRequest;
import com.travel.itinerary.travelItinerary.Services.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MailController {
    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendEmail(@Valid @RequestBody EmailRequest emailRequest){
        try{
            emailService.sendEmail(emailRequest);
            return ResponseEntity.ok(ApiResponse.success("Email sent successfully"));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Unexpected error"));
        }
    }
}
