package sample.taskapp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sample.taskapp.Model.Category;
import sample.taskapp.Model.Task;
import sample.taskapp.Model.User;
import sample.taskapp.Repos.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    private JavaMailSender mailSender;

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

    public Page<Task> filterTasks(User user, Long categoryId, String status, Pageable pageable) {
        if (categoryId != null && status != null && !status.isEmpty()) {
            return taskRepository.findByUserAndCategoryIdAndStatus(user, categoryId, status, pageable);
        }
        else if (categoryId != null) {
            return taskRepository.findByUserAndCategoryId(user, categoryId, pageable);
        }
        else if (status != null && !status.isEmpty()) {
            return taskRepository.findByUserAndStatus(user, status, pageable);
        }
        else {
            return taskRepository.findByUser(user, pageable);
        }
    }

    public Page<Task> searchTaskByTitle(User user, String title, Pageable pageable) {
        return taskRepository.findByUserAndTitleContainingIgnoreCase(user, title, pageable);
    }

    public void sendEmailTaskCreated(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Task added succesfully");
        message.setText("Task was succesfully added to your account");

        mailSender.send(message);
    }

}
