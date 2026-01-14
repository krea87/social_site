package se.jensen.johan.socialsite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jensen.johan.socialsite.dto.LoginRequestDTO;
import se.jensen.johan.socialsite.dto.LoginResponseDTO;
import se.jensen.johan.socialsite.service.TokenService;

@RestController
@RequestMapping("/request-token")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDTO> token(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.username(),
                        loginRequestDTO.password()
                )
        );

        // När auth är klar skickar vidare till TokenService. Innehåll:
        // användarnamn, roller, utgånstid, signatur
        String token = tokenService.generateToken(auth);

        //returnerar i ett objekt (LoginResponseDTO) och inte en rå sträng
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
