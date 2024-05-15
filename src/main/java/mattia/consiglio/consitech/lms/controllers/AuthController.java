package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.JWTDTO;
import mattia.consiglio.consitech.lms.payloads.LoginAuthDTO;
import mattia.consiglio.consitech.lms.payloads.NewUserDTO;
import mattia.consiglio.consitech.lms.services.AuthService;
import mattia.consiglio.consitech.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/auth")
public class AuthController extends BaseController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @PostMapping("login")
    public JWTDTO login(@RequestBody @Validated LoginAuthDTO loginAuthDTO, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return authService.login(loginAuthDTO);
    }

    @PostMapping("revalidate")
    public JWTDTO revalidate(@RequestBody @Validated JWTDTO jwtDTO, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return authService.revalidate(jwtDTO);
    }


    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Validated NewUserDTO userDTO, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return userService.createUser(userDTO);
    }
}
