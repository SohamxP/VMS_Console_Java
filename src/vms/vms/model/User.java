package vms.model;

public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String role;

    public User(String id, String name, String email, String passwordHash, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" + id + ", " + name + ", " + email + ", " + role + "}";
    }
}
