package com.travel.itinerary.travelItinerary.Services;

import com.travel.itinerary.travelItinerary.Dto.EmailRequest;
import com.travel.itinerary.travelItinerary.Dto.ItineraryRequest;
import com.travel.itinerary.travelItinerary.Dto.ItineraryResponse;
import com.travel.itinerary.travelItinerary.Model.Itinerary;
import com.travel.itinerary.travelItinerary.Model.User;
import com.travel.itinerary.travelItinerary.Repository.ItineraryRepository;
import com.travel.itinerary.travelItinerary.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItineraryService {
    private final ItineraryRepository itineraryRepository;
    private final TextProcessingService textProcessingService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private ItineraryResponse modelToResponse(Itinerary itinerary){
        return ItineraryResponse.builder()
                .id(itinerary.getId())
                .budgetRange(itinerary.getBudgetRange())
                .fullItinerary(itinerary.getFullItinerary())
                .travelStyle(itinerary.getTravelStyle())
                .createdAt(itinerary.getCreatedAt())
                .updatedAt(itinerary.getUpdatedAt())
                .numberOfDays(itinerary.getNumberOfDays())
                .destination(itinerary.getDestination())
                .startDate(itinerary.getStartDate())
                .endDate(itinerary.getEndDate())
                .build();
    }

    public ItineraryResponse getItineraryById(Long userId, Long itineraryId){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        Itinerary itinerary = itineraryRepository.findById(itineraryId).orElseThrow(()-> new RuntimeException("Itinerary not found"));
        if(!itinerary.getUser().getId().equals(userId)){
            throw new RuntimeException("Access denied");
        }
        return modelToResponse(itinerary);
    }

    public void deleteItineraryById(Long userId, Long itineraryId){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        Itinerary itinerary = itineraryRepository.findById(itineraryId).orElseThrow(()-> new RuntimeException("Itinerary not found"));
        if(!itinerary.getUser().getId().equals(userId)){
            throw new RuntimeException("Access denied");
        }
        itineraryRepository.delete(itinerary);
    }

    public ItineraryResponse saveItinerary(Long userId, ItineraryRequest itineraryRequest){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        Itinerary itinerary = Itinerary.builder()
                .user(user)
                .destination(itineraryRequest.getDestination())
                .fullItinerary(itineraryRequest.getFullItinerary())
                .startDate(itineraryRequest.getStartDate())
                .endDate(itineraryRequest.getEndDate())
                .numberOfDays(itineraryRequest.getNumberOfDays())
                .budgetRange(itineraryRequest.getBudgetRange())
                .travelStyle(itineraryRequest.getTravelStyle())
                .build();

        Itinerary savedItinerary = itineraryRepository.save(itinerary);
        return modelToResponse(savedItinerary);
    }

    public List<ItineraryResponse> getUserItineraries(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        List<Itinerary> itineraries = itineraryRepository.findByUserOrderByCreatedAtDesc(user);

        return itineraries.stream()
                .map(this::modelToResponse)
                .collect(Collectors.toList());
    }

    public List<ItineraryResponse> searchItineraries(Long userId, String searchItr){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));

        String processedItr = textProcessingService.processText(searchItr);
        List<Itinerary> itineraries = itineraryRepository.findByUserOrderByCreatedAtDesc(user);
        return itineraries.stream().filter(
                itinerary -> {
                    String processedDestination = textProcessingService.processText(itinerary.getDestination());
                    String processedItinerary = textProcessingService.processText(itinerary.getFullItinerary());

                    return processedDestination.contains(processedItr) || processedItinerary.contains(processedItr);
                }
        ).map(this::modelToResponse).collect(Collectors.toList());
    }

    public void sendEmail(User user, ItineraryRequest request) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setMessage("Thank you for checking your travel itinerary with us! We hope the AI-powered recommendations have helped you plan your trip.\n" +
                "\n" +
                "For even better support and personalized assistance, we invite you to explore our premium plans, starting at very affordable prices. With premium access, you'll enjoy priority support and exclusive features tailored to make your travel experience even more seamless.\n" +
                "\n" +
                "If you have any questions or need help enrolling, feel free to reach out!");
        emailRequest.setTo(user.getEmail());
        emailRequest.setSubject("Thank You for Checking Your Itinerary! ✨");
        emailService.sendEmail(emailRequest);
    }



}
