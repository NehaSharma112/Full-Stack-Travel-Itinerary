package com.travel.itinerary.travelItinerary.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE )
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    String username;
    String email;
    @NotBlank(message = "Password is required")
    String password;
}
