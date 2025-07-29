package com.travel.itinerary.travelItinerary.Dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE )
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {
    String userName;
    String phone;
}
