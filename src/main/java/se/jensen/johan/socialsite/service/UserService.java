package se.jensen.johan.socialsite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import se.jensen.johan.socialsite.dto.PostResponseDTO;
import se.jensen.johan.socialsite.dto.UserRequestDTO;
import se.jensen.johan.socialsite.dto.UserResponseDTO;
import se.jensen.johan.socialsite.dto.UserWithPostsResponseDTO;
import se.jensen.johan.socialsite.exception.GlobalExceptionHandler;
import se.jensen.johan.socialsite.mapper.UserMapper;
import se.jensen.johan.socialsite.model.User;
import se.jensen.johan.socialsite.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    


    public UserService(UserRepository userRepository, UserMapper userMapper, GlobalExceptionHandler globalExceptionHandler, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;

        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole(),
                        user.getDisplayName(),
                        user.getBio(),
                        user.getProfileImagePath()
                ))
                .toList();
    }

    public UserWithPostsResponseDTO getUserWithPosts(Long id) {
        User user = userRepository.findUserWithPosts(id)
                .orElseThrow(() -> {
                    logger.warn("Misslyckades att hämta användare med inlägg. Ingen användare hittades med id: {}", id);
                    return new NoSuchElementException("Ingen user i databasen med id: " + id);
                });

        UserWithPostsResponseDTO dto = userMapper.toWithPostsDto(user);

        return dto;
    }

    public UserResponseDTO addUser(UserRequestDTO userDto) {
        User user = UserMapper.fromDto(userDto);

        // Kollar om username eller email finns
        boolean exists = userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (exists) {
            logger.warn("Kunde inte lägga till användare. Användarnamn '{}' eller e-post '{}' finns redan.", user.getUsername(), user.getEmail());
            throw new IllegalArgumentException("User eller email finns redan i databasen");
        }

        // Hashar lösenordet
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));

        User savedUsed = userRepository.save(user);
        logger.info("Ny användare skapad med id: {} och användarnamn: {}", savedUsed.getId(), savedUsed.getUsername());

        return UserMapper.toDto(savedUsed);
    }

    public UserResponseDTO findById(Long id) {

        //Optional-variant för att hitta user med id med en hjälpmetod

        User user = findUserOrThrow(id);
        return UserMapper.toDto(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponseDTO getUserResponseByUsername(String username) {
        User user = getUserByUsername(username);
        return UserMapper.toDto(user);
    }


    public UserResponseDTO update(Long id, UserRequestDTO dto) {

        //Optional-variant för att hitta user med id med en hjälpmetod

        User user = findUserOrThrow(id);

        // sätter nya värden
        user.setUsername(dto.username());
        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        user.setRole(dto.role());
        user.setDisplayName(dto.displayName());
        user.setBio(dto.bio());
        user.setProfileImagePath(dto.profileImagePath());

        // uppdaterar och sparar de nya värdena i repository
        User updated = userRepository.save(user);

        // returnerar den uppdaterade
        return UserMapper.toDto(updated);
    }

    public void delete(Long id) {
        // Optional
        User user = findUserOrThrow(id);

        // Raderar den hittade usern
        userRepository.deleteById(id);
        logger.info("Användare med id {} har raderats.", id);
    }


    // lägga denna i en utility-klass senare kanske
    public User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Sökning misslyckades: Ingen användare hittades med id: {}", id);
                    return new NoSuchElementException("Ingen user i databasen med id: " + id);
                });
    }
}
