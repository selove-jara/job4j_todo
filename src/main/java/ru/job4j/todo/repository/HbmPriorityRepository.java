package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmPriorityRepository implements PriorityRepository {

    private final CrudRepository crudRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(HbmTaskRepository.class);

    @Override
    public Optional<Priority> findById(int id) {
        try {
            return crudRepository.optional("from Priority WHERE id = :id", Priority.class, Map.of("id", id));
        } catch (Exception e) {
            LOGGER.error("Ошибка при поиске задачи с id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Priority> findAllOrderById() {
        try {
            return crudRepository.query("from Priority ORDER BY id", Priority.class);
        } catch (Exception e) {
            LOGGER.error("Ошибка при получении всех задач", e);
            return List.of();
        }
    }
}
