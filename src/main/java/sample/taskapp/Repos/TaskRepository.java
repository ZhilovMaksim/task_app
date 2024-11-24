package sample.taskapp.Repos;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import sample.taskapp.Model.Category;
import sample.taskapp.Model.Task;
import sample.taskapp.Model.User;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);

    List<Task> findByUserAndCategoryIdAndStatus(User user, Long categoryId, String status, Sort sort);

    List<Task> findByUserAndStatus(User user, String status, Sort sort);
    List<Task> findByUserAndCategoryId(User user, Long categoryId, Sort sort);
    List<Task> findByUser(User user, Sort sort);
}
