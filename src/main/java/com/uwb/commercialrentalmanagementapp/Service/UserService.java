package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Enum.UserRole;
import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean authenticate(String username, String password) {
        // Pobierz użytkownika z bazy danych na podstawie nazwy użytkownika
        User user = userRepository.findByUsername(username);

        if (user != null) {
            // Haszuj podane hasło i porównaj z hasłem w bazie danych
            String hashedPassword = hashPassword(password);
            return hashedPassword.equals(user.getPassword());
        }

        return false;
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
    private String hashPassword(String password) {
        if (password != null) {
            // Wybierz algorytm haszujący (SHA-256 w tym przypadku)
            return DigestUtils.sha256Hex(password);
        } else {
            // Obsłuż sytuację, gdy hasło jest null
            throw new IllegalArgumentException("Password cannot be null");
        }
    }

    //metoda potrzebna przy dodawaniu użytkownika z panelu admina
    public String hashAndSaltPassword(String password) {
        if (password != null) {
            // Wybierz algorytm haszujący (SHA-256 w tym przypadku)
            return hashPassword(password);
        } else {
            // Obsłuż sytuację, gdy hasło jest null
            throw new IllegalArgumentException("Password cannot be null");
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void register(String username, String password, String firstName, String lastName, String email, UserRole role) {
        User newUser = new User();
        newUser.setUsername(username);
        String hashedPassword = hashPassword(password);
        newUser.setPassword(hashedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setRole(role.getRoleName());
        // Dodaj inne pola użytkownika, np. imię, nazwisko, e-mail
        userRepository.save(newUser);
    }

    public String getRole(Long userId){
        return userRepository.findRole(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }


}
