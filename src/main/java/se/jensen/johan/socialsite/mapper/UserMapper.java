package se.jensen.johan.socialsite.mapper;

import org.springframework.stereotype.Component;
import se.jensen.johan.socialsite.dto.UserRequestDTO;
import se.jensen.johan.socialsite.dto.UserResponseDTO;
import se.jensen.johan.socialsite.model.User;

@Component
public class UserMapper {
    public static User fromDto(UserRequestDTO dto) {
        User user = new User();

        user.setProfileImagePath(dto.profileImagePath());
        user.setRole(dto.role());
        user.setPassword(dto.password());
        user.setEmail(dto.email());
        user.setBio(dto.bio());
        user.setUsername(dto.username());
        user.setDisplayName(dto.displayName());

        return user;
    }

    public static UserResponseDTO toDto(User user) {
        UserResponseDTO dto = new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getDisplayName(),
                user.getBio(),
                user.getProfileImagePath());
        return dto;
    }
}
