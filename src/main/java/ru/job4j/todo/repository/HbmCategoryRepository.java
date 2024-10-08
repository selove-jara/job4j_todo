package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmCategoryRepository implements CategoryRepository {

    private final CrudRepository crudRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(HbmTaskRepository.class);

    @Override
    public Optional<Category> findById(int id) {
        try {
            return crudRepository.optional("from Category WHERE id = :id", Category.class, Map.of("id", id));
        } catch (Exception e) {
            LOGGER.error("Ошибка при поиске задачи с id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Category> findAllOrderById() {
        try {
            return crudRepository.query("from Category ORDER BY id", Category.class);
        } catch (Exception e) {
            LOGGER.error("Ошибка при получении всех задач", e);
            return List.of();
        }
    }

    @Override
    public List<Category> findByListOfId(List<Integer> categoriesId) {
        try {
            return crudRepository.query(
                    "FROM Category WHERE id IN :fCategoriesId", Category.class,
                    Map.of("fCategoriesId", categoriesId)
            );
        } catch (Exception e) {
            LOGGER.error("Exception on find Category ById", e);
        }
        return Collections.emptyList();
    }
}
