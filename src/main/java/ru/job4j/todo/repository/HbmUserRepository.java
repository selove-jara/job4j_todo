package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmUserRepository implements UserRepository {

    private final CrudRepository crudRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(HbmUserRepository.class);

    @Override
    public Optional<User> save(User user) {
        try {
            crudRepository.run(session -> session.save(user));
            return Optional.of(user);
        } catch (Exception e) {
            LOGGER.error("Ошибка при сохранении пользователя: {}", user, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try {
            return crudRepository.optional(
                    "FROM User WHERE email = :fEmail AND password = :fPassword",
                    User.class,
                    Map.of("fEmail", email, "fPassword", password)
            );
        } catch (Exception e) {
            LOGGER.error("Ошибка при поиске пользователя по email и паролю. Email: {}", email, e);
            return Optional.empty();
        }
    }
}