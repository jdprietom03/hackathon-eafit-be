package com.cp.retry.shared.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    @Value("${jwt.service.secret}")
    private String JWT_SECRET;
    @Value("${jwt.service.ttl}")
    private long JWT_EXPIRATION;
    private static final String REDIS_PREFIX = "auth:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Object login(Object userCredentials) {
        String username = ""; //Get User
        String rol = ""; //Get Rol
        List<String> roles = Collections.singletonList(rol);
        String token = generateJwtToken(username, roles);

        if (Objects.nonNull(token)) {
            saveInRedis(token, username);
        }

        return null; //Return User Information
    }

    public void saveInRedis(String token, String username) {
        // Almacena el token JWT en Redis
        String key = REDIS_PREFIX + token;
        redisTemplate.opsForValue().set(key, username);
        redisTemplate.expire(key, 1, TimeUnit.HOURS); 
    }

    public Claims validateTokenAndAuthenticate(String token) {
        try {
            String key = REDIS_PREFIX + token;
            String username = (String) redisTemplate.opsForValue().get(key);

            if (Objects.nonNull(username)) {
                return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
            }

        } catch (Exception e) { }

        return null;
    }

    private String generateJwtToken(String username, List<String> roles) {
        List<GrantedAuthority> grantedAuthorities = roles.stream()
                .map(AuthorityUtils::commaSeparatedStringToAuthorityList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("authorities",
                        grantedAuthorities.stream().map(GrantedAuthority::getAuthority)
                                .toList())
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

}
