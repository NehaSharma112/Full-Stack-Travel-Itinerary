package com.travel.itinerary.travelItinerary.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtHelper {
    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    @Value("{jwt.expiration}")
    private String expiration;

    private PrivateKey getPrivateKey(){
        try {
            String privateKeyContent = Files.readString(Paths.get(privateKeyPath))
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(privateKeyContent);
            //PKCS8EncodedKeySpec is used for private keys
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch(IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new RuntimeException("Failed to load private key", e);
        }

    }
    private PublicKey getPublicKey(){
        try{
            String publicKeyContent = Files.readString(Paths.get(publicKeyPath))
                    .replace("-----BEGIN PUBLIC KEY-----","")
                    .replace("-----END PUBLIC KEY-----","")
                    .replaceAll("\\s","");

            byte[] decoded = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }catch(IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(expiration)))
                .compact();
    }

    //this func is to read the token
    private Claims extractAllClaims(String token) {//extracts the claim of token
        return Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token){//gives expiration time
        return extractClaims(token, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token){//check if expired or not
        return extractExpiration(token).before(new Date());
    }

    public String extractUserName(String token){//return username from token
        return extractClaims(token, Claims::getSubject);
    }
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUserName(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

}
