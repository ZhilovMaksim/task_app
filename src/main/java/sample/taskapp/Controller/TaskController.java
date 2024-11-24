package sample.taskapp.Controller;

import ch.qos.logback.core.joran.conditional.IfAction;
import jakarta.validation.Valid;
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

    public TaskController(TaskService taskService, CategoryService categoryService, UserService userService, UserRepository userRepository) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.userRepository = userRepository;
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

    @GetMapping("/")
    public String getAllSortedTask(@RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false) Long categoryId,
                                   @RequestParam(required = false) String status,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails,
                                   Model model) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            return "redirect:/auth/signin";
        }
        List<Task> tasks;

        Sort sort = (sortBy != null && !sortBy.isEmpty()) ? Sort.by(sortBy) : Sort.unsorted();

        tasks = taskService.filterTasks(user, categoryId, status, sort);

        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "task";
    }
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks/";
    }
}
