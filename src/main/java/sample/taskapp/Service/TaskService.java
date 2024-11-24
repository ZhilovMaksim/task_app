package sample.taskapp.Service;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sample.taskapp.Model.Category;
import sample.taskapp.Model.Task;
import sample.taskapp.Model.User;
import sample.taskapp.Repos.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTaskByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public List<Task> filterTasks(User user, Long categoryId, String status, Sort sort) {
        if (categoryId != null && status != null && !status.isEmpty()) {
            return taskRepository.findByUserAndCategoryIdAndStatus(user, categoryId, status, sort);
        }
        else if (categoryId != null) {
            return taskRepository.findByUserAndCategoryId(user, categoryId, sort);
        }
        else if (status != null && !status.isEmpty()) {
            return taskRepository.findByUserAndStatus(user, status, sort);
        }
        else {
            return taskRepository.findByUser(user, sort);
        }
    }
}
