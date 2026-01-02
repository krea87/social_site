package se.jensen.johan.socialsite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "Användarnamn måste vara mellan 3 och 30 tecken långt") @Size(min = 3, max = 30) String username,
        @NotBlank(message = "Lösenord måste vara mellan 8 och 64 tecken långt") @Size(min = 8, max = 64) String password, @NotBlank(message = "Roll får inte vara tom") @Size(min = 3, max = 10) String role, String email, String displayName, String bio, String profileImagePath)
         {
}
