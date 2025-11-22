package vms.model;

public class Admin extends User {
    public Admin(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash, "ADMIN");
    }
}
