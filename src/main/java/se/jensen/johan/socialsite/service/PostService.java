package se.jensen.johan.socialsite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.jensen.johan.socialsite.dto.PostRequestDTO;
import se.jensen.johan.socialsite.dto.PostResponseDTO;
import se.jensen.johan.socialsite.model.Post;
import se.jensen.johan.socialsite.model.User;
import se.jensen.johan.socialsite.repository.PostRepository;
import se.jensen.johan.socialsite.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(UserRepository userRepository, PostRepository postRepository, UserService userService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public PostResponseDTO createPost(Long userId, PostRequestDTO postDto){
        logger.info("Försöker skapa ett nytt inlägg för användare med id: {}", userId);
        
        Post post = new Post();
        post.setText(postDto.text());
        post.setCreatedAt(LocalDateTime.now());

        User user = userService.findUserOrThrow(userId);

        post.setUser(user);

        Post savedPost = postRepository.save(post);
        logger.info("Inlägg skapat med id: {} för användare: {}", savedPost.getId(), userId);

        return new PostResponseDTO(savedPost.getId(), savedPost.getText(), savedPost.getCreatedAt());
    }
}
