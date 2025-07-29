package com.travel.itinerary.travelItinerary.Services;

import com.travel.itinerary.travelItinerary.Dto.AuthResponse;
import com.travel.itinerary.travelItinerary.Dto.LoginRequest;
import com.travel.itinerary.travelItinerary.Dto.SignUpRequest;
import com.travel.itinerary.travelItinerary.Dto.UserProfileRequest;
import com.travel.itinerary.travelItinerary.Model.User;
import com.travel.itinerary.travelItinerary.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtHelper jwtHelper;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse signup(SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            //already exist
            throw new RuntimeException("Email Already Exist");
        }

        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            //already exist
            throw new RuntimeException("Username Already Exist");
        }

        String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .username(signUpRequest.getUsername())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .password(hashedPassword)
                .phoneNumber(signUpRequest.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);
        // Generate JWT token
        String token = jwtHelper.generateToken(savedUser.getUsername());
        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole()
        );

    }

    public AuthResponse login(LoginRequest loginRequest) throws BadRequestException {
        if(loginRequest.getUsername()==null || loginRequest.getUsername().trim().isEmpty()
        && loginRequest.getEmail()==null || loginRequest.getEmail().trim().isEmpty()){
            throw new BadRequestException("Username or Email is required");
        }

        User user = null;

        if(loginRequest.getEmail()!=null || !loginRequest.getUsername().trim().isEmpty()){
            user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        }else if(loginRequest.getUsername()!=null || !loginRequest.getEmail().trim().isEmpty()){
            user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        }

        if(user == null) throw new BadRequestException("USer does not exists");
//        String passwordByUser = passwordEncoder.encode(loginRequest.getPassword());
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadRequestException("Invalid Password");
        }
        String token = jwtHelper.generateToken(user.getUsername());
        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    public User getUserByUserName(UserProfileRequest userProfileRequest) {
        return userRepository.findByUsername(userProfileRequest.getUserName()).orElse(null);
    }

    public Boolean checkByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public Boolean checkByUserName(String username) {
        return userRepository.existsByUsername(username);
    }
}
