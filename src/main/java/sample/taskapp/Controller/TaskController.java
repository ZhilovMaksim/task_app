package sample.taskapp.Controller;

import ch.qos.logback.core.joran.conditional.IfAction;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sample.taskapp.Model.Category;
import sample.taskapp.Model.Task;
import sample.taskapp.Model.User;
import sample.taskapp.Model.UserDetailsImpl;
import sample.taskapp.Repos.TaskRepository;
import sample.taskapp.Repos.UserRepository;
import sample.taskapp.Service.CategoryService;
import sample.taskapp.Service.TaskService;
import sample.taskapp.Service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final CategoryService categoryService;

    private final UserService userService;

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService,
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

    @PostMapping
    public String createTask(@ModelAttribute @Valid Task task,
                             Model model,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

//        if (bindingResult.hasErrors()) {
//            model.addAttribute("tasks", task);
//            model.addAttribute("categories", categoryService.getAllCategories());
//            return "task-create";
//        }

        User user = userService.findUserById(userDetails.getId());
        task.setUser(user);
        taskService.saveTask(task);
        return "redirect:/tasks/";
    }

    @GetMapping("/create")
    public String showTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "task-create";
    }

//    @GetMapping("/")
//    public String getAllTasks(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
//        List<Task> tasks = taskService.getTaskByUserId(userDetails.getId());
//        model.addAttribute("tasks", tasks);
//        model.addAttribute("categories", categoryService.getAllCategories());
//        return "task";
//    }

    @GetMapping("/search")
    public String searchTaskByTitle(@RequestParam String title,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    Model model) {
        if (userDetails == null) {
            System.out.println("UserDetails is null. Redirecting to login...");
            return "redirect:/auth/signin";  // Редирект, если пользователь не аутентифицирован
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasksPage = taskService.searchTaskByTitle(user, title, pageable);

        model.addAttribute("tasks", tasksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tasksPage.getTotalPages());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "task";
    }

    @GetMapping("/")
    public String getAllSortedTask(@RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false) Long categoryId,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails,
                                   Model model) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            return "redirect:/auth/signin";
        }
        Page<Task> tasksPage;

        Sort sort = (sortBy != null && !sortBy.isEmpty()) ? Sort.by(sortBy) : Sort.unsorted();
        Pageable pageable = PageRequest.of(page, size, sort);

        tasksPage = taskService.filterTasks(user, categoryId, status, pageable);

        model.addAttribute("tasks", tasksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tasksPage.getTotalPages());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "task";
    }
    @GetMapping("/{id}/edit")
    public String editTask(@PathVariable Long id, Model model) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            model.addAttribute("task", task);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "edit-task";
        } else {
            return "redirect:/tasks/";
        }
    }
    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task updatedTask = existingTask.get();
            updatedTask.setTitle(task.getTitle());
            updatedTask.setDescription(task.getDescription());
            updatedTask.setDueDate(task.getDueDate());
            updatedTask.setCategory(task.getCategory());
            updatedTask.setStatus(task.getStatus());
            updatedTask.setPriority(task.getPriority());

            taskRepository.save(updatedTask);
        }
        return "redirect:/tasks/";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks/";
    }
}
