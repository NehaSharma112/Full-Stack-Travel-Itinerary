package com.travel.itinerary.travelItinerary.Controller;

import com.travel.itinerary.travelItinerary.Dto.*;
import com.travel.itinerary.travelItinerary.Model.User;
import com.travel.itinerary.travelItinerary.Services.UserService;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
@Slf4j//for logger
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    Logger logger = Logger.getLogger(String.valueOf(AuthController.class));
//    @Autowired
//    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody SignUpRequest signUpRequest){
        System.out.println("Request comes here");
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
        System.out.println("api hits");
        try{
            AuthResponse authResponse = userService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("User Logged in Successfully ",authResponse ));
        }catch(BadRequestException e){
            logger.log(Level.INFO, "Error while Login" + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(@Valid @RequestBody UserProfileRequest userProfileRequest){
        try{
            User user = userService.getUserByUserName(userProfileRequest);
            return ResponseEntity.ok(ApiResponse.success("Profile fetched ",user ));
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
