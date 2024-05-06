package mattia.consiglio.consitech.lms.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
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

    public String generateToken(User user) {

        long iatMs = System.currentTimeMillis();
        return Jwts.builder()
                .issuedAt(new Date(iatMs))
                .expiration(new Date(iatMs + 1000 * 60 * 60)) // 1 hour
                .subject(user.getId().toString())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parse(token);
        } catch (ExpiredJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid token");
        }
    }

    public String getSubjectFromToken(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parseSignedClaims(token).getPayload().getSubject();
    }

}
