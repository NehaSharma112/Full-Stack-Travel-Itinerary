package com.travel.itinerary.travelItinerary.Repository;

import com.travel.itinerary.travelItinerary.Model.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
}
