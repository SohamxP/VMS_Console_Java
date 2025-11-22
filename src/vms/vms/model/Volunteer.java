package vms.model;

public class Volunteer extends User {
    public Volunteer(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash, "VOLUNTEER");
    }
}
