package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmTaskRepository implements TaskRepository {
    private final CrudRepository crudRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(HbmTaskRepository.class);

    @Override
    public Task save(Task task) {
        try {
            crudRepository.run(session -> session.persist(task));
            return task;
        } catch (Exception e) {
            LOGGER.error("Ошибка при сохранении задачи: {}", task, e);
            return null;
        }
    }

    @Override
    public boolean update(Task task) {
        try {
            return crudRepository.run(session -> session.merge(task));
        } catch (Exception e) {
            LOGGER.error("Ошибка при обновлении задачи: {}", task, e);
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            return crudRepository.run(
                    "delete from Task where id = :fId",
                    Map.of("fId", id)
            );
        } catch (Exception e) {
            LOGGER.error("Ошибка при удалении задачи с id: {}", id, e);
            return false;
        }
    }

    @Override
    public Optional<Task> findById(int id) {
        try {
            return crudRepository.optional("from Task f JOIN FETCH f.priority JOIN FETCH f.categories WHERE f.id = :fId", Task.class, Map.of("fId", id));
        } catch (Exception e) {
            LOGGER.error("Ошибка при поиске задачи с id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Task> findAllOrderById() {
        try {
            return crudRepository.query("from Task f JOIN FETCH f.priority JOIN FETCH f.categories ORDER BY f.id", Task.class);
        } catch (Exception e) {
            LOGGER.error("Ошибка при получении всех задач", e);
            return List.of();
        }
    }

    @Override
    public List<Task> findByStatus(boolean status) {
        try {
            return crudRepository.query("from Task f JOIN FETCH f.priority WHERE done = :fDone", Task.class, Map.of("fDone", status));
        } catch (Exception e) {
            LOGGER.error("Ошибка при поиске задач по статусу: {}", status, e);
            return List.of();
        }
    }

    @Override
    public boolean complete(int id) {
        try {
            return crudRepository.run("UPDATE Task SET done = true WHERE id = :fId", Map.of("fId", id));
        } catch (Exception e) {
            LOGGER.error("Ошибка при завершении задачи с id: {}", id, e);
            return false;
        }
    }
}