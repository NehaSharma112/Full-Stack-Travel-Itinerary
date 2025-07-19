package com.travel.itinerary.travelItinerary.Model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE )
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String username;
    String email;
    String password;
    String firstName;
    String lastName;
    String phoneNumber;
    LocalDate createdAt;
    LocalDate updatedAt;

    @Column(name = "is_enabled")
    boolean enabled;
    private String role;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDate.now();
        updatedAt=LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt=LocalDate.now();
    }
}
