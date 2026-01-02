package se.jensen.johan.socialsite.dto;

import java.time.LocalDateTime;

public record PostResponseDTO(Long id, String text, LocalDateTime createdAt) {
}
