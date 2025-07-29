package com.travel.itinerary.travelItinerary.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryRequest {
    @NotBlank(message = "Destination is required")
    private String destination;

    @NotBlank(message = "Full itinerary is required")
    private String fullItinerary;

    private String startDate;
    private String endDate;
    private Integer numberOfDays;
    private String budgetRange;
    private String travelStyle;
}
