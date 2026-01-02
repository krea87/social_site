package se.jensen.johan.socialsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.jensen.johan.socialsite.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
