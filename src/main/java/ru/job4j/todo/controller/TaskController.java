package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

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
    public String save(@ModelAttribute Task task) {
        taskService.save(task);
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") int id) {
        taskService.delete(id);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}")
    public String getTask(@PathVariable("id") int id, Model model) {
        var task = taskService.findById(id);
        model.addAttribute("task", task.get());
        return "tasks/one";
    }

    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable("id") int id) {
        Optional<Task> optionalTask = taskService.findById(id);
        Task task = optionalTask.get();
        task.setDone(true);
        taskService.update(task);
        return "redirect:/tasks/" + id;
    }

    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable("id") int id, Model model) {
        Optional<Task> optionalTask = taskService.findById(id);
        model.addAttribute("task", optionalTask.get());
        return "tasks/edit";
    }

    @PostMapping("/edit/{id}")
    public String editTask(@PathVariable("id") int id, @RequestParam String title, @RequestParam String description) {
        Optional<Task> optionalTask = taskService.findById(id);
        Task task = optionalTask.get();
        task.setTitle(title);
        task.setDescription(description);
        taskService.update(task);
        return "redirect:/tasks";
    }
}