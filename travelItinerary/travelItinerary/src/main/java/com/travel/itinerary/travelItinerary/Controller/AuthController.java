package com.travel.itinerary.travelItinerary.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
@Slf4j//for logger
@RequiredArgsConstructor
public class AuthController {
    Logger logger = Logger.getLogger(String.valueOf(AuthController.class));

}
