package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.entities.UserRole;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.EditUserDTO;
import mattia.consiglio.consitech.lms.payloads.NewUserDTO;
import mattia.consiglio.consitech.lms.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(NewUserDTO userDTO) {
        if (userRepository.existsByUsernameOrEmail(userDTO.username(), userDTO.email())) {
            throw new BadRequestException("Username and email already in use");
        } else if (userRepository.existsByUsername(userDTO.username())) {
            throw new BadRequestException("Username already in use");
        } else if (userRepository.existsByEmail(userDTO.email())) {
            throw new BadRequestException("Email already in use");
        }
        User user = new User(userDTO.username(), userDTO.email(), passwordEncoder.encode(userDTO.password()), UserRole.USER);
        return userRepository.save(user);
    }

    public User createUser(EditUserDTO userDTO) {
        if (userRepository.existsByUsernameOrEmail(userDTO.username(), userDTO.email())) {
            throw new BadRequestException("Username and email already in use");
        } else if (userRepository.existsByUsername(userDTO.username())) {
            throw new BadRequestException("Username already in use");
        } else if (userRepository.existsByEmail(userDTO.email())) {
            throw new BadRequestException("Email already in use");
        }
        User user = new User(userDTO.username(), userDTO.email(), passwordEncoder.encode(userDTO.password()), UserRole.valueOf(userDTO.role()));
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new BadRequestException("User not found"));
    }

    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(() -> new BadRequestException("User not found"));
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException("User not found"));
    }

    public Page<User> getUsers(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return userRepository.findAll(pageable);
    }

    public User updateUser(UUID userId, NewUserDTO userDTO) {
        User user = this.getUserById(userId);
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        return userRepository.save(user);
    }

    public User updateUser(UUID userId, EditUserDTO userDTO) {
        User user = this.getUserById(userId);
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setRole(UserRole.valueOf(userDTO.role()));
        return userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        User user = this.getUserById(userId);
        userRepository.delete(user);
    }

}
