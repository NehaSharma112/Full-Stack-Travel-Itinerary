package com.travel.itinerary.travelItinerary.Model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE )
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Itinerary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)//Load the related entity only when it’s accessed
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    String destination;

    @Column(columnDefinition = "TEXT")
    String fullItinerary;

    @Column(name = "start_date")
    String startDate;

    @Column(name = "end_date")
    String endDate;

    @Column(name = "number_of_days")
    Integer numberOfDays;

    @Column(name = "budget_range")
    String budgetRange;

    @Column(name = "travel_style")
    String travelStyle;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
