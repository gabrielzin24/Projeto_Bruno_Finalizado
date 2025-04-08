package service;

import model.User;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static final Map<String, User> registeredUsers = new HashMap<>();
    public static final int AUTH_SUCCESS = 0;
    public static final int AUTH_USER_NOT_FOUND = 1;
    public static final int AUTH_INVALID_PASSWORD = -1;
    private static int userIndex = -1;
    
    public User registerUser(String name, String email, String password) {
        if (registeredUsers.containsKey(email)) {
            return null;
        }
        
        User newUser = new User(++userIndex, name, email, password);
        registeredUsers.put(email, newUser);
        
        return newUser;
    }

    public User getUserByEmail(String email) {
        return registeredUsers.get(email);
    }

    public int authenticate(String email, String password) {
        User user = registeredUsers.get(email);
        
        if (user == null) return AUTH_USER_NOT_FOUND;
        if (!user.getPassword().equals(password)) return AUTH_INVALID_PASSWORD;
        
        return AUTH_SUCCESS;
    }
}