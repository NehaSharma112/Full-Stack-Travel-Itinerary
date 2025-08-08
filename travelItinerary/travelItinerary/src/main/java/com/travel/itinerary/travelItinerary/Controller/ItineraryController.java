package com.travel.itinerary.travelItinerary.Controller;


import com.travel.itinerary.travelItinerary.Dto.ApiResponse;
import com.travel.itinerary.travelItinerary.Dto.ItineraryRequest;
import com.travel.itinerary.travelItinerary.Dto.ItineraryResponse;
import com.travel.itinerary.travelItinerary.Model.User;
import com.travel.itinerary.travelItinerary.Repository.UserRepository;
import com.travel.itinerary.travelItinerary.Services.ItineraryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itineraries")
@Slf4j//for logger
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class ItineraryController {
    private final UserRepository userRepository;
    private final ItineraryService itineraryService;
    private String getAuthenticatedUsername(HttpServletRequest request) {
        return (String) request.getAttribute("authenticatedUsername");
    }


    private boolean validateUserAccess(String tokenUsername, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return tokenUsername.equals(user.getUsername());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ItineraryResponse>> saveItinerary(HttpServletRequest request, @RequestParam Long userId,
                                                                   @Valid @RequestBody ItineraryRequest itineraryRequest){

        System.out.println("Backend called");
        try{
            String tokenUserName = getAuthenticatedUsername(request);
            if(!validateUserAccess(tokenUserName,userId)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Token User does not match here"));
            }
            ItineraryResponse response = itineraryService.saveItinerary(userId,itineraryRequest);
            return ResponseEntity.ok(ApiResponse.success("Itinerary saved successfully",response));
        }catch (Exception e){
            log.error("Error saving itinerary {}",e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public  ResponseEntity<ApiResponse<List<ItineraryResponse>>> getUserItineraries(HttpServletRequest request,
                                                                                    Long userId){
        try{
            String tokenUserName = getAuthenticatedUsername(request);
            if(!validateUserAccess(tokenUserName,userId)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Token User does not match here"));
            }
            List<ItineraryResponse> itineraries = itineraryService.getUserItineraries(userId);
            return ResponseEntity.ok(ApiResponse.success("Itineraries retrieved successfully",itineraries));
        }catch(Exception e){
            log.error("Error while retrieving itineraries ",e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ItineraryResponse>>> searchItineraries(HttpServletRequest request,
                                                                            @RequestParam Long userId,
                                                                            @RequestParam String searchItenerary){
        try{
            String tokenUserName = getAuthenticatedUsername(request);
            if(!validateUserAccess(tokenUserName,userId)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Token User does not match here"));
            }
            List<ItineraryResponse> itineraries = itineraryService.searchItineraries(userId, searchItenerary);
            return ResponseEntity.ok(ApiResponse.success("Search completed successfully",itineraries));
        }catch(Exception e){
            log.error("Error while searching itineraries ",e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{itineraryId}")
    public ResponseEntity<ApiResponse<String>> deleteItinerary(HttpServletRequest request,
                                                               @RequestParam Long userId,
                                                               @PathVariable Long itineraryId){
        try{
            String tokenUserName = getAuthenticatedUsername(request);
            if(!validateUserAccess(tokenUserName,userId)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Token User does not match here"));
            }
            itineraryService.deleteItineraryById(userId, itineraryId);
            return ResponseEntity.ok(ApiResponse.success("Deleted successfully",null));
        }catch(Exception e){
            log.error("Error deleting itineraries ",e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
