package com.monthly.ecommercemonolith.security.jwt;

import com.monthly.ecommercemonolith.security.services.UserDetailsImpl;
import com.monthly.ecommercemonolith.security.services.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtils {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;
    @Value("${spring.app.jwtCookie}")
    private String jwtCookie;

    // getting JWT from header
//    public String getJWTFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("Authorization Header {}:", bearerToken);
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }

    public ResponseCookie generateJWTCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie =
                ResponseCookie.from(jwtCookie, jwt).path("/api")
                        .maxAge(24 * 60 * 60)
                        .httpOnly(false).build();
        return cookie;
    }

    public ResponseCookie getCleanCookie(){
        return ResponseCookie.from(jwtCookie,null).path("/api").build();
    }

    public String getJWTFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    // generating  token from username
    public String generateTokenFromUsername(String username) {
//        String username = userDetails.getUsername();
        return Jwts.builder().subject(username).issuedAt(new Date()).expiration(new Date(new Date().getTime() + jwtExpirationMs)).signWith(key()).compact();
    }

    // getting username from jwt token
    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    // generate signing key
    public Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    // validate jwt token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT exception: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired:{}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported:{}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty:{}", e.getMessage());
        }
        return false;
    }
}
