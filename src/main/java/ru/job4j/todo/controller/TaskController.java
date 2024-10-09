package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskService;
import ru.job4j.todo.service.UserService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final PriorityService priorityService;

    @GetMapping
    public String getAll(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        var tasks = taskService.findAllOrderById();
        tasks.forEach(task ->
            task.setCreated(task.getCreated()
                    .atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of(user.getTimezone()))
                    .toLocalDateTime())
        );
        model.addAttribute("tasks", tasks);
        return "tasks/list";
    }

    @GetMapping("/status")
    public String getTasksByStatus(@RequestParam boolean done, Model model) {
        List<Task> tasks = taskService.findByStatus(done);
        model.addAttribute("tasks", tasks);
        return "tasks/list";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("priorities", priorityService.findAllOrderById());
        model.addAttribute("categories", categoryService.findAllOrderById());
        return "tasks/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Task task, HttpSession session, int priorityId, @RequestParam List<Integer> categoryId) {
        User user = (User) session.getAttribute("user");
        Priority priority = priorityService.findById(priorityId).get();
        task.setUser(user);
        task.setPriority(priority);
        taskService.save(task, user, priority, categoryId);
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") int id, Model model) {
        boolean rsl = taskService.delete(id);
        if (!rsl) {
            model.addAttribute("message", "Не удалось удалить задачу");
            return "errors/404";
        }
        return "redirect:/tasks";
    }

    @GetMapping("/{id}")
    public String getTask(@PathVariable("id") int id, Model model) {
        var task = taskService.findById(id);
        if (task.isEmpty()) {
            model.addAttribute("message", "Задача не найдена");
            return "errors/404";
        }
        model.addAttribute("task", task.get());
        return "tasks/one";
    }

    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable("id") int id, Model model) {
        boolean rsl = taskService.complete(id);
        if (!rsl) {
            model.addAttribute("message", "Не удалось обновить задачу");
            return "errors/404";
        }
        return "redirect:/tasks/" + id;
    }

    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable("id") int id, Model model) {
        Optional<Task> optionalTask = taskService.findById(id);
        List<Priority> priorities = priorityService.findAllOrderById();
        if (optionalTask.isEmpty()) {
            model.addAttribute("message", "Задача не найдена");
            return "errors/404";
        }
        model.addAttribute("task", optionalTask.get());
        model.addAttribute("priorities", priorities);
        return "tasks/edit";
    }

    @PostMapping("/edit/{id}")
    public String editTask(@ModelAttribute Task task, Model model, HttpSession session, int priorityId) {
        User user = (User) session.getAttribute("user");
        Priority priority = priorityService.findById(priorityId).get();
        task.setPriority(priority);
        task.setUser(user);

        boolean rsl = taskService.update(task);
        if (!rsl) {
            model.addAttribute("message", "Не удалось обновить задачу");
            return "errors/404";
        }
        return "redirect:/tasks";
    }
}