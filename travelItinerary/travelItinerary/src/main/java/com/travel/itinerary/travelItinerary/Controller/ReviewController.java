package com.travel.itinerary.travelItinerary.Controller;

import com.travel.itinerary.travelItinerary.Dto.ApiResponse;
import com.travel.itinerary.travelItinerary.Dto.ReviewRequest;
import com.travel.itinerary.travelItinerary.Dto.ReviewResponse;
import com.travel.itinerary.travelItinerary.Services.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews(){
        try{
            List<ReviewResponse> reviewResponses = reviewService.getAllReviews();
            return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully",reviewResponses));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> saveReview(HttpServletRequest request, @Valid @RequestBody ReviewRequest reviewRequest){
        try{
            String tokenUserName=(String) request.getAttribute("authenticatedUsername");
            if(tokenUserName==null)
            {
                return ResponseEntity.status(401).body(ApiResponse.error("Authentication failed"));
            }
            ReviewResponse reviewResponse=reviewService.saveReview(reviewRequest, tokenUserName);
            return ResponseEntity.ok(ApiResponse.success("Submitted success", reviewResponse));
        }catch(Exception e){
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        }
    }
}
