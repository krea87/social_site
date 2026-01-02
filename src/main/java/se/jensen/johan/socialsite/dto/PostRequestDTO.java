package se.jensen.johan.socialsite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequestDTO(
        @NotBlank(message = "Texten måste vara minst 1 tecken långt.") @Size(max = 3000) String text) {
}
