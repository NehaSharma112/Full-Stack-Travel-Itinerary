package com.travel.itinerary.travelItinerary.Dto;

import com.travel.itinerary.travelItinerary.Model.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private String reviewerName;
    private String title;
    private String destination;
    private LocalDateTime createdAt;
}
