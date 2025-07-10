package com.eaglebank.api.service.jwt;


public interface JwtService {

    String generateToken(String username);

    String validateTokenAndGetUsername(String token);
}
