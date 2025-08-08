package com.travel.itinerary.travelItinerary.Services;

import com.travel.itinerary.travelItinerary.Dto.ReviewRequest;
import com.travel.itinerary.travelItinerary.Dto.ReviewResponse;
import com.travel.itinerary.travelItinerary.Model.Review;
import com.travel.itinerary.travelItinerary.Model.User;
import com.travel.itinerary.travelItinerary.Repository.ReviewRepository;
import com.travel.itinerary.travelItinerary.Repository.UserRepository;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    private ReviewResponse reviewReqToReviewResp(Review review){
        return ReviewResponse.builder().id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewerName(review.getReviewerName())
                .title(review.getTitle())
                .destination(review.getDestination())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private ReviewResponse reviewToReviewResponse(Review review){
        return ReviewResponse.builder()
                .reviewerName(review.getReviewerName())
                .rating(review.getRating())
                .comment(review.getComment())
                .title(review.getTitle())
                .destination(review.getDestination())
                .createdAt(review.getCreatedAt())
                .build();
    }


    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAllByOrderByCreatedAtDesc();
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for(Review review:reviews){
            reviewResponses.add(reviewReqToReviewResp(review));
        }
        return reviewResponses;
    }

    public ReviewResponse saveReview(ReviewRequest reviewRequest, String userName) {
        User user = userRepository.findByUsername(userName).orElse(null);
//        review.setReviewerName(userName);
        if(user == null) throw new RuntimeException();
        Review review = reviewReqToReview(reviewRequest,userName,user);

        Review savedReview = reviewRepository.save(review);
        return reviewToReviewResponse(savedReview);
    }

    private Review reviewReqToReview(ReviewRequest reviewRequest, String userName, User user){
        return Review.builder()
                .reviewerName(userName)
                .user(user)
                .rating(reviewRequest.getRating())
                .comment(reviewRequest.getComment())
                .title(reviewRequest.getTitle())
                .destination(reviewRequest.getDestination())
                .build();
    }


}
