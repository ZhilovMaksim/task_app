package sample.taskapp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sample.taskapp.Model.Task;
import sample.taskapp.Model.User;
import sample.taskapp.Model.UserDetailsImpl;
import sample.taskapp.Repos.TaskRepository;
import sample.taskapp.Repos.UserRepository;
import sample.taskapp.Service.CategoryService;
import sample.taskapp.Service.TaskService;
import sample.taskapp.Service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final TaskService taskService;
    private final CategoryService categoryService;

    private final UserService userService;

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    public AdminController(TaskService taskService,
                          CategoryService categoryService,
                          UserService userService,
                          UserRepository userRepository,
                          TaskRepository taskRepository) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }



    @GetMapping("/dashboard")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-dashboard";
    }

    @GetMapping("/user-tasks/{id}")
    public String viewUserTasks(@PathVariable Long id, Model model) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("tasks", taskRepository.findByUserId(id));
            return "user-tasks";
        }
        else return "redirect:/admin/dashboard";
    }

    @GetMapping("user-tasks/{id}/create-task")
    public String showTaskForm(Model model,
                               @PathVariable Long id) {
        User user = userService.findUserById(id);
        if (user == null) {
            return "redirect:/admin/user-taks";
        }
        Task task = new Task();
        task.setUser(user);
        model.addAttribute("task", task);
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/admin/user-tasks/" + id;
    }

    @PostMapping("/user-tasks/{id}/tasks")
    public String createTaskByAdmin(@ModelAttribute Task task,
                                    @PathVariable Long id) {
        User user = userService.findUserById(id);
        System.out.println(user.getId());
        task.setUser(user);
        taskRepository.save(task);
        taskService.sendEmailTaskCreated(user);
        return "redirect:/admin/user-tasks/" + id;
    }

//    @GetMapping("/{id}/edit")
//    public String editTask(@PathVariable Long id, Model model) {
//        Optional<Task> taskOptional = taskRepository.findById(id);
//        if (taskOptional.isPresent()) {
//            Task task = taskOptional.get();
//            model.addAttribute("task", task);
//            model.addAttribute("categories", categoryService.getAllCategories());
//            return "edit-task";
//        } else {
//            return "redirect:/admin/dashboard";
//        }
//    }
//    @PostMapping("/{id}/edit")
//    public String updateTask(@PathVariable Long id, @ModelAttribute Task task) {
//        Optional<Task> existingTask = taskRepository.findById(id);
//        if (existingTask.isPresent()) {
//            Task updatedTask = existingTask.get();
//            updatedTask.setTitle(task.getTitle());
//            updatedTask.setDescription(task.getDescription());
//            updatedTask.setDueDate(task.getDueDate());
//            updatedTask.setCategory(task.getCategory());
//            updatedTask.setStatus(task.getStatus());
//            updatedTask.setPriority(task.getPriority());
//
//            taskRepository.save(updatedTask);
//        }
//        return "redirect:/admin/dashboard";
//    }
//
    @PostMapping("user-tasks/{userId}/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable Long userId,
                             @PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/admin/user-tasks/" + userId;
    }



}
