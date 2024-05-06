package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.EditUserDTO;
import mattia.consiglio.consitech.lms.payloads.NewUserDTO;
import mattia.consiglio.consitech.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUserById(@PathVariable("id") UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    public User getMe(@AuthenticationPrincipal User user) {
        return user;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "username") String sort) {
        return userService.getUsers(page, size, sort);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public User updateUser(@PathVariable("id") UUID id, @Validated @RequestBody EditUserDTO user, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return userService.updateUser(id, user);
    }

    @PutMapping("/me")
    public User updateMe(@AuthenticationPrincipal User user, @Validated @RequestBody NewUserDTO userDto, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return userService.updateUser(user.getId(), userDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
    }

}
