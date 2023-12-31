package KlajdiNdoci.Capstone.repositories;

import KlajdiNdoci.Capstone.entities.Comment;
import KlajdiNdoci.Capstone.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByUser(User user, Pageable pageable);
}
