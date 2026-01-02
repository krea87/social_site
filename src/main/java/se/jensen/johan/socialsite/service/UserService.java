package se.jensen.johan.socialsite.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GlobalExceptionHandler globalExceptionHandler;


    public UserService(UserRepository userRepository, UserMapper userMapper, GlobalExceptionHandler globalExceptionHandler) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.globalExceptionHandler = globalExceptionHandler;
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
                .orElseThrow(() -> new NoSuchElementException("Ingen user i databasen med id: " + id));

        List<PostResponseDTO> posts = user.getPosts()
                .stream()
                .map(p -> new PostResponseDTO(
                        p.getId(),
                        p.getText(),
                        p.getCreatedAt()
                ))
                .toList();
        UserResponseDTO dto = new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getDisplayName(),
                user.getBio(),
                user.getProfileImagePath()
        );
        UserWithPostsResponseDTO dtoToReturn =
                new UserWithPostsResponseDTO(dto, posts);

        return dtoToReturn;
    }

    public UserResponseDTO addUser(UserRequestDTO userDto) {
        User user = UserMapper.fromDto(userDto);

        // Kollar om username eller email finns
        boolean exists = userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (exists) {
            throw new IllegalArgumentException("User eller email finns redan i databasen");
        }
        User savedUsed = userRepository.save(user);

        return UserMapper.toDto(savedUsed);
    }

    public UserResponseDTO findById(Long id) {

        //Optional-variant för att hitta user med id med en hjälpmetod

        User user = findUserOrThrow(id);
        return UserMapper.toDto(user);
    }

    public UserResponseDTO update(Long id, UserRequestDTO dto) {

        //Optional-variant för att hitta user med id med en hjälpmetod

        User user = findUserOrThrow(id);

        // sätter nya värden
        user.setUsername(dto.username());
        user.setPassword(dto.password());
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
    }


    // lägga denna i en utility-klass senare kanske
    public User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ingen user i databasen med id: " + id));
    }
}
