package vms.service;

import vms.model.*;
import java.util.*;

public class AuthService {
    private final DataStore db;

    public AuthService(DataStore db) {
        this.db = db;
    }

    public User login(String email, String password) {
        List<User> users = db.loadUsers();
        String hash = DataStore.sha256(password);
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(email) && u.getPasswordHash().equals(hash))
                return u;
        return null;
    }
}
