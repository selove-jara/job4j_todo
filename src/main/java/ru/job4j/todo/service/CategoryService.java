package ru.job4j.todo.service;

import ru.job4j.todo.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Optional<ru.job4j.todo.model.Category> findById(int id);

    List<ru.job4j.todo.model.Category> findAllOrderById();

    List<Category> findByListOfId(List<Integer> categoriesId);
}
