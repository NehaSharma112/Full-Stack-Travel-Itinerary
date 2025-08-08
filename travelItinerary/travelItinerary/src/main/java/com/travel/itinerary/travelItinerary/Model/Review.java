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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    private Integer rating;

    @Column
    private String comment;

    @Column
    private String reviewerName;

    @Column
    private String title;
    @Column
    private String destination;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate()
    {
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate()
    {
        updatedAt=LocalDateTime.now();
    }
}
