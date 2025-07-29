package com.travel.itinerary.travelItinerary.Dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE )
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    String token;
    String type = "Bearer";
    Long id;
    String userName;
    String email;
    String firstName;
    String lastName;
    String role;
}
