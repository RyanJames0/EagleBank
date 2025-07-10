package com.eaglebank.api.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eaglebank.api.model.user.User;
import com.eaglebank.api.service.jwt.JwtService;
import com.eaglebank.api.service.user.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  @Autowired private JwtService jwtService;
  @Autowired private UserService userService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
      String path = request.getServletPath();
      String method = request.getMethod();

      return (path.equals("/v1/users") && method.equals("POST")) || 
              path.startsWith("/h2-console");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
    throws ServletException, IOException {

      String authHeader = request.getHeader("Authorization");
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
          String token = authHeader.substring(7);
          try {
              String username = jwtService.validateTokenAndGetUsername(token);
              User user = userService.getUserByEmail(username);
              
              if (user != null) {
                  UsernamePasswordAuthenticationToken auth =
                      new UsernamePasswordAuthenticationToken(username, null, List.of());
                  SecurityContextHolder.getContext().setAuthentication(auth);
              }

          } catch (Exception e) {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              return;
          }

      }
      filterChain.doFilter(request, response);
    }
}
