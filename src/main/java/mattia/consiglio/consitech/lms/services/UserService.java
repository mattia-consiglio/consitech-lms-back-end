package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.entities.enums.UserRole;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.*;
import mattia.consiglio.consitech.lms.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(NewUserDTO userDTO) {
        this.userChecks(userDTO.username(), userDTO.email());
        User user = new User(userDTO.username(), userDTO.email(), passwordEncoder.encode(userDTO.password()), UserRole.USER);
        return userRepository.save(user);
    }

    public User createUser(EditUserDTO userDTO) {
        this.userChecks(userDTO.username(), userDTO.email());
        User user = new User(userDTO.username(), userDTO.email(), passwordEncoder.encode(userDTO.password()), UserRole.valueOf(userDTO.role()));
        return userRepository.save(user);
    }

    public void userChecks(String username, String email) {
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new BadRequestException("Username and email already in use");
        } else if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username already in use");
        } else if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already in use");
        }
    }

    public IsAvailableDTO isUsernameAvailable(String username, User user) {
        return new IsAvailableDTO(userRepository.isUsernameAvailable(username, user));
    }

    public IsAvailableDTO isEmailAvailable(String email, User user) {
        return new IsAvailableDTO(userRepository.isEmailAvailable(email, user));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(() -> new ResourceNotFoundException("User", "username or email", usernameOrEmail));
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public Page<User> getUsers(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return userRepository.findAll(pageable);
    }

    public int countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    public User updateUser(User user, UserFullUpdateDTO userDTO, Boolean updatePassword) {
        if (userRepository.existsByUsername(userDTO.username())) {
            throw new BadRequestException("Username already in use");
        }
        if (userRepository.existsByEmail(userDTO.email())) {
            throw new BadRequestException("Email already in use");
        }
        if (updatePassword) {
            this.PasswordCheck(user, userDTO.newPassword(), userDTO.oldPassword(), userDTO.username(), userDTO.email());
            user.setPassword(passwordEncoder.encode(userDTO.newPassword()));
        }
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        return userRepository.save(user);
    }

    public User updateUser(User user, UserFullUpdateDTO userDTO) {
        return this.updateUser(user, userDTO, true);
    }

    public User updateUser(User user, UserPartialUpdateDTO userDTO) {
        UserFullUpdateDTO userDTO1 = new UserFullUpdateDTO(userDTO.username(), "", "", userDTO.email());
        return this.updateUser(user, userDTO1, false);
    }

    public User updateUser(UUID userId, UserFullUpdateDTO userDTO) {
        User user = this.getUserById(userId);
        return this.updateUser(user, userDTO);
    }

    public void PasswordCheck(User user, String newPassword, String oldPassword, String username, String email) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is not correct");
        } else if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("New password cannot be the same as old password");
        } else if (newPassword.contains(username) || newPassword.contains(email)) {
            throw new BadRequestException("New password cannot be the same as username or email");
        }
    }

    public User updateUserPassword(User user, UserPasswordDTO userDTO) {
        this.PasswordCheck(user, userDTO.newPassword(), userDTO.oldPassword(), user.getUsername(), user.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.newPassword()));
        return userRepository.save(user);
    }

    public User updateUserPassword(UUID userId, UserPasswordDTO userDTO) {
        User user = this.getUserById(userId);
        return this.updateUserPassword(user, userDTO);
    }

    public User updateUserRole(UUID userId, UserRoleDTO userRoleDTO) {
        User user = this.getUserById(userId);
        user.setRole(UserRole.valueOf(userRoleDTO.role()));
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void deleteUser(UUID userId) {
        User user = this.getUserById(userId);
        this.deleteUser(user);
    }
}
