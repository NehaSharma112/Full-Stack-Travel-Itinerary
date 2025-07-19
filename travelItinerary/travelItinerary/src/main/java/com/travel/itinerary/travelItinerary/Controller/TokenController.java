package com.travel.itinerary.travelItinerary.Controller;

import com.travel.itinerary.travelItinerary.Dto.ApiResponse;
import com.travel.itinerary.travelItinerary.Dto.AuthResponse;
import com.travel.itinerary.travelItinerary.Dto.LoginRequest;
import com.travel.itinerary.travelItinerary.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/token")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class TokenController {
    private final UserService userService;
    Logger logger = Logger.getLogger(String.valueOf(TokenController.class));

    @GetMapping("/generate")
    public ResponseEntity<ApiResponse<AuthResponse>> generateToken(@Valid @RequestBody LoginRequest loginRequest){
        try{
            AuthResponse authResponse = userService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("Token generated successfully",authResponse));
        }catch(Exception e){
            logger.log(Level.FINE, "Error while generating token: " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
