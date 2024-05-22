package mattia.consiglio.consitech.lms.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTTools {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration}")
    private int expiration;


    public String generateToken(User user) {

        long iatMs = System.currentTimeMillis();
        return Jwts.builder()
                .issuedAt(new Date(iatMs))
                .expiration(new Date(iatMs + expiration)) // 1 hour
                .subject(user.getId().toString())
                .issuer(issuer)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parser()
                    .requireIssuer(issuer)
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid token");
        }
    }

    public String getSubjectFromToken(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parseSignedClaims(token).getPayload().getSubject();
    }

}
