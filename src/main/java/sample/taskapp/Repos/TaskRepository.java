package sample.taskapp.Repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import sample.taskapp.Model.Category;
import sample.taskapp.Model.Task;
import sample.taskapp.Model.User;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUserAndCategoryIdAndStatus(User user, Long categoryId, String status, Pageable pageable);
    Page<Task> findByUserAndCategoryId(User user, Long categoryId, Pageable pageable);
    Page<Task> findByUserAndStatus(User user, String status, Pageable pageable);
    Page<Task> findByUser(User user, Pageable pageable);
    Page<Task> findByUserAndTitleContainingIgnoreCase(User user, String title, Pageable pageable);
    List<Task> findByUserId(Long id);
}
