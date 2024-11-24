package sample.taskapp.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import sample.taskapp.Model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoryById(Long categoryId);
}
