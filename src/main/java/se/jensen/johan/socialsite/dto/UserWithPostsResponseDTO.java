package se.jensen.johan.socialsite.dto;

import java.util.List;

public record UserWithPostsResponseDTO(UserResponseDTO user, List<PostResponseDTO> posts) {
}
