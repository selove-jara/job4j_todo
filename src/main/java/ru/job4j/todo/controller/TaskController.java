package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.TaskService;
import ru.job4j.todo.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAllOrderById());
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
        return "tasks/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Task task, HttpSession session) {
        User user = (User) session.getAttribute("user");
        task.setUser(user);
        taskService.save(task, user);
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
        if (optionalTask.isEmpty()) {
            model.addAttribute("message", "Задача не найдена");
            return "errors/404";
        }
        model.addAttribute("task", optionalTask.get());
        return "tasks/edit";
    }

    @PostMapping("/edit/{id}")
    public String editTask(@ModelAttribute Task task, Model model) {
        boolean rsl = taskService.update(task);
        if (!rsl) {
            model.addAttribute("message", "Не удалось обновить задачу");
            return "errors/404";
        }
        return "redirect:/tasks";
    }
}