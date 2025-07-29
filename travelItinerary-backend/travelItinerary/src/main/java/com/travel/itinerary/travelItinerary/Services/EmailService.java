package com.travel.itinerary.travelItinerary.Services;

import com.travel.itinerary.travelItinerary.Dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("{spring.mail.username}")
    private String fromEmail;
//    private String to;
//    private String subject;
//    private String message;
//    String cc;
//    String bcc;
    public void sendEmail(EmailRequest emailRequest){
        try{

            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(emailRequest.getSubject());
            message.setTo(emailRequest.getTo());
            message.setFrom(fromEmail);
            message.setText(emailRequest.getMessage());

            if(emailRequest.getCc()!=null && !emailRequest.getCc().trim().isEmpty()) {
                message.setBcc(emailRequest.getBcc());
            }

            if(emailRequest.getBcc()!=null && !emailRequest.getBcc().trim().isEmpty()) {
                message.setCc(emailRequest.getCc());
            }

            javaMailSender.send(message);
        }catch (Exception e){
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
