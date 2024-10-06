package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmUserRepository implements UserRepository {

    private final SessionFactory sf;

    @Override
    public Optional<User> save(User user) {
        Optional<User> returnUser = Optional.empty();
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            returnUser = Optional.of(user);
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return returnUser;
    }

    @Override
    public Optional<User> findByEmailAndPassword(String login, String password) {
        Optional<User> returnUser = Optional.empty();
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            var query = session.createQuery("FROM User WHERE email = :fEmail AND password = :fPassword", User.class)
                    .setParameter("fEmail", login)
                    .setParameter("fPassword", password);
            returnUser = query.uniqueResultOptional();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return returnUser;
    }
}
