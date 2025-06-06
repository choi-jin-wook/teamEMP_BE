package emp.emp.community.repository;

import emp.emp.community.entity.Post;
import emp.emp.community.enums.HealthCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByHealthCategory(HealthCategory healthCategory);
}
