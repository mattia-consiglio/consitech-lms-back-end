package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.*;
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
import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@RestController
@RequestMapping(BASE_URL + "/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUserById(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        return userService.getUserById(uuid);
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

    @GetMapping("/username/{username}/available")
    public IsAvailableDTO isUsernameAvailable(@AuthenticationPrincipal User user, @PathVariable("username") String username) {
        return userService.isUsernameAvailable(username, user);
    }

    @GetMapping("/email/{email}/available")
    public IsAvailableDTO isEmailAvailable(@AuthenticationPrincipal User user, @PathVariable("email") String email) {
        return userService.isEmailAvailable(email, user);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User updateUser(@PathVariable("id") String id, @Validated @RequestBody UserRoleDTO userRoleDTO, BindingResult validation) {
        UUID uuid = checkUUID(id, "id");
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return userService.updateUserRole(uuid, userRoleDTO);
    }

    @PutMapping("/me/password")
    public User updatePassword(@AuthenticationPrincipal User user, @Validated @RequestBody UserPasswordDTO userPasswordDTO, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return userService.updateUserPassword(user, userPasswordDTO);
    }

    @PutMapping("/me/full")
    public User updateMe(@AuthenticationPrincipal User user, @Validated @RequestBody UserFullUpdateDTO userDto, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return userService.updateUser(user, userDto);
    }

    @PutMapping("/me")
    public User updateMe(@AuthenticationPrincipal User user, @Validated @RequestBody UserPartialUpdateDTO userDto, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return userService.updateUser(user, userDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        userService.deleteUser(uuid);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
    }
}
