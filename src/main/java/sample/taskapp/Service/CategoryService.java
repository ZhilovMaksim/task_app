package sample.taskapp.Service;

import org.springframework.stereotype.Service;
import sample.taskapp.Model.Category;
import sample.taskapp.Repos.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    public List<Category> findById(Long categoryId) {
        return categoryRepository.findCategoryById(categoryId);
    }
}
