package com.travel.itinerary.travelItinerary.Repository;

import com.travel.itinerary.travelItinerary.Model.Itinerary;
import com.travel.itinerary.travelItinerary.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {

    List<Itinerary> findByUserOrderByCreatedAtDesc(User user);
}
