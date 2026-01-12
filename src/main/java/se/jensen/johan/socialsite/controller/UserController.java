package se.jensen.johan.socialsite.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import se.jensen.johan.socialsite.dto.*;
import se.jensen.johan.socialsite.model.Post;
import se.jensen.johan.socialsite.model.User;
import se.jensen.johan.socialsite.repository.UserRepository;
import se.jensen.johan.socialsite.service.PostService;
import se.jensen.johan.socialsite.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final PostService postService;
    private UserService userService;

    public List<User> users = new ArrayList<>();

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @PreAuthorize( "hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        // skapar en userResponseDto-lista och kallar på metoden från userService.
        List<UserResponseDTO> usersFromDb = userService.getAllUsers();


        // returnerar 200 ok och listan
        return ResponseEntity.ok(usersFromDb);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {

        // Skapar en userresponse dto och använder serviceklassens adduser
        UserResponseDTO created = userService.addUser(dto);


        // returnerar 201 created och med user created
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{userId}/posts")
    public ResponseEntity<PostResponseDTO> createPostForUser(
            @PathVariable Long userId, @Valid @RequestBody PostRequestDTO requestDto) {

        PostResponseDTO responseDTO = postService.createPost(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {

        UserResponseDTO userById = userService.findById(id);


        return ResponseEntity.ok(userById);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public UserResponseDTO getMe (Authentication authentication){
        String username = authentication.getName();
        return userService.getUserResponseByUsername(username);
    }


    @GetMapping("/{id}/with-posts")
    public ResponseEntity<UserWithPostsResponseDTO> getUserWithPosts(@PathVariable Long id) {
        UserWithPostsResponseDTO response = userService.getUserWithPosts(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO dto) {

        UserResponseDTO response = userService.update(id, dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
