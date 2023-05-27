package com.cp.retry.shared.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cp.retry.shared.services.AuthenticationService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                
        try {
            if (!existsToken(request)) {
                SecurityContextHolder.clearContext();
            } else {
                Claims claims = validateToken(request);

                if (Objects.isNull(claims.get("authorities"))) {
                    SecurityContextHolder.clearContext();
                } else {
                    setUpSpringAuthentication(claims);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("Error with Authentication: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private void setUpSpringAuthentication(Claims claims) {
        List<String> authorities = (List) claims.get("authorities");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList());

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private Claims validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");

        return authenticationService.validateTokenAndAuthenticate(jwtToken);
    }

    private boolean existsToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(HEADER);

        return Objects.nonNull(authenticationHeader) && authenticationHeader.startsWith(PREFIX);
    }
}
