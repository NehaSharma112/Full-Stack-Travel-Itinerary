package com.travel.itinerary.travelItinerary.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private Integer rating;
    private String comment;
    private String reviewerName;
    private String title;
    private String destination;
}
