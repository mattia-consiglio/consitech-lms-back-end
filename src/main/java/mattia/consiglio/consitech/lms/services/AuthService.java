package mattia.consiglio.consitech.lms.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.exceptions.UnauthorizedException;
import mattia.consiglio.consitech.lms.payloads.JWTDTO;
import mattia.consiglio.consitech.lms.payloads.LoginAuthDTO;
import mattia.consiglio.consitech.lms.security.JWTTools;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserService utenteService;
    private final JWTTools jwtTools;
    private final PasswordEncoder passwordEncoder;

    public JWTDTO login(LoginAuthDTO loginAuthDTO) {
        User user = utenteService.getUserByUsernameOrEmail(loginAuthDTO.usernameOrEmail());
        if (user == null || !passwordEncoder.matches(loginAuthDTO.password(), user.getPassword())) {
            throw new UnauthorizedException("Credentials not valid. Try login again");
        }
        return new JWTDTO(jwtTools.generateToken(user));
    }

    public JWTDTO revalidate(JWTDTO jwtDTO) {
        Jwt<JwsHeader, Claims> jwt = jwtTools.validateToken(jwtDTO.authorization());

        if (jwt == null) {
            throw new UnauthorizedException("Credentials not valid. Try login again");
        }
        User user = utenteService.getUserByUsernameOrEmail(jwt.getPayload().getSubject());
        return new JWTDTO(jwtTools.generateToken(user));
    }

}
