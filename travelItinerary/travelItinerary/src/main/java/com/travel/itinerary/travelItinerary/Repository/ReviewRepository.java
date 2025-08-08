package com.travel.itinerary.travelItinerary.Repository;

import com.travel.itinerary.travelItinerary.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    List<Review> findAllByOrderByCreatedAtDesc();
}
