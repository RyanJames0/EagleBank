package com.eaglebank.api.service.jwt;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
  // WARNING: In production, never hardcode SECRET_KEY.
  // Use a secure environment variable or a secrets manager instead.
  final String secretKey = "your-very-long-secret-key-of-at-least-32-bytes";
    
  @Override
  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 mins
        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String validateTokenAndGetUsername(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey.getBytes())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }
}
