package se.jensen.johan.socialsite.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.jensen.johan.socialsite.dto.PostRequestDTO;
import se.jensen.johan.socialsite.dto.PostResponseDTO;
import se.jensen.johan.socialsite.model.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private List<Post> posts = new ArrayList<>();

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {


        // Skapar en lista av PostResponseDTO genom att mappa varje post i listan
        List<PostResponseDTO> response =
                posts.stream()
                        .map(post -> new PostResponseDTO(
                                post.getId(),
                                post.getText(),
                                post.getCreatedAt()
                        ))
                        .toList();

        // 200 ok + DTO
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO dto) {

        //skapar post och lägger till i listan
        Post post = new Post();
        post.setId(0L);
        post.setText(dto.text());
        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        posts.add(post);

        // Skapar responseDTO
        PostResponseDTO response =
                new PostResponseDTO(
                        post.getId(),
                        post.getText(),
                        post.getCreatedAt());
        // 201 created + DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {

        // kontrollerar om index är inom listans gränser
        if (id < 0 || id >= posts.size()) {
            return ResponseEntity.notFound().build();
        }

        // hämtar posten från listan
        Post p = posts.get(id.intValue());


        // skapar responseDTO
        PostResponseDTO response =
                new PostResponseDTO(0L,
                        p.getText(),
                        p.getCreatedAt());

        // 200 ok DTO
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequestDTO dto) {

        // kontrollerar om index är inom listans gränser
        if (id < 0 || id >= posts.size()) {
            return ResponseEntity.notFound().build();
        }

        // hämtar posten från listan
        Post post = posts.get(id.intValue());

        // uppdatera värden från DTO
        post.setText(dto.text());

        // Skapar responseDTO
        PostResponseDTO response =
                new PostResponseDTO(
                        id,
                        post.getText(),
                        post.getCreatedAt()
                );

        // 200 ok + DTO
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {

        // kontrollerar om index är inom listans gränser
        if (id < 0 || id >= posts.size()) {
            return ResponseEntity.notFound().build();
        }

        posts.remove(id);
        return ResponseEntity.noContent().build();
    }
}
