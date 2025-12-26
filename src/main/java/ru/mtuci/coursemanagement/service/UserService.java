package ru.mtuci.coursemanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mtuci.coursemanagement.model.User;
import ru.mtuci.coursemanagement.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String u) {
        return repo.findByUsername(u);
    }

    /**
     * Сохраняем пользователя так, чтобы пароль никогда не попадал в БД в открытом
     * виде.
     */
    public User save(User u) {
        if (u.getPassword() != null && !u.getPassword().isBlank()) {
            // ВАЖНО: не кодируем повторно, если пароль уже BCrypt-хэш
            if (!u.getPassword().startsWith("$2a$") &&
                    !u.getPassword().startsWith("$2b$") &&
                    !u.getPassword().startsWith("$2y$")) {
                u.setPassword(passwordEncoder.encode(u.getPassword()));
            }
        }
        return repo.save(u);
    }
}
