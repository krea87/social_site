package se.jensen.johan.socialsite.dto;

public record UserResponseDTO(Long id, String username, String email, String role, String displayName, String bio, String profileImagePath) {
}
