package com.travel.itinerary.travelItinerary.Controller;

import com.travel.itinerary.travelItinerary.Dto.*;
import com.travel.itinerary.travelItinerary.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
@Slf4j//for logger
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    Logger logger = Logger.getLogger(String.valueOf(AuthController.class));

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody SignUpRequest signUpRequest){
        try{
            AuthResponse authResponse = userService.signup(signUpRequest);
            return ResponseEntity.ok(ApiResponse.success("User Registered Successfully ",authResponse ));
        }catch(RuntimeException e){
            logger.log(Level.INFO, "Error while SignUp" + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest){
        try{
            AuthResponse authResponse = userService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("User Logged in Successfully ",authResponse ));
        }catch(BadRequestException e){
            logger.log(Level.INFO, "Error while Login" + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AuthResponse>> getProfile(@Valid @RequestBody UserProfileRequest userProfileRequest){
        try{
            AuthResponse authResponse = userService.getProfile(userProfileRequest);
            return ResponseEntity.ok(ApiResponse.success("Profile fetched ",authResponse ));
        }catch(RuntimeException e){
            logger.log(Level.INFO, "Error while fetching profile " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

    }

    @GetMapping("check-username/{username}")
    public  ResponseEntity<ApiResponse<Boolean>> checkUsernameAvailability(@PathVariable String username){
        try{
            Boolean isAlreadyPresent = userService.checkByUserName(username);
            return ResponseEntity.ok(ApiResponse.success("User Name available", !isAlreadyPresent));
        }catch(Exception e){
            logger.log(Level.INFO,"Error while getting user by username"+e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
