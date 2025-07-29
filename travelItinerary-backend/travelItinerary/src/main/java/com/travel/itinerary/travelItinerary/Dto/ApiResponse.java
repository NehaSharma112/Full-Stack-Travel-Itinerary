package com.travel.itinerary.travelItinerary.Dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE )
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    boolean success;
    String message;
    T data;

    public static <T> ApiResponse<T> success(String message, T data){
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        return apiResponse;
    }

    public static <T> ApiResponse<T> success(String message){
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage(message);
        apiResponse.setData(null);
        return apiResponse;
    }

    public static <T> ApiResponse<T> error(String message, T data){
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        return apiResponse;
    }

    public static <T> ApiResponse<T> error(String message){
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(message);
        apiResponse.setData(null);
        return apiResponse;
    }
}
