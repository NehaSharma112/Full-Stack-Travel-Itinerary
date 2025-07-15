package com.travel.itinerary.travelItinerary.Dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE )
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryResponse {
    Long id;
    String destination;
    String fullItinerary;
    String startDate;
    String endDate;
    Integer numberOfDays;
    String budgetRange;
    String travelStyle;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
