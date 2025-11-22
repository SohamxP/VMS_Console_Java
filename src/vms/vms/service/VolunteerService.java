package vms.service;

import vms.model.*;
import java.util.*;

public class VolunteerService {
    private final DataStore db;

    public VolunteerService(DataStore db) {
        this.db = db;
    }

    public Volunteer register(String name, String email, String password) {
        User created = db.addVolunteer(name, email, password);
        return created == null ? null : (Volunteer) created;
    }

    public List<Volunteer> allVolunteers() {
        List<Volunteer> list = new ArrayList<>();
        for (User u : db.loadUsers())
            if ("VOLUNTEER".equals(u.getRole()))
                list.add((Volunteer) u);
        return list;
    }

    public Volunteer findById(String id) {
        for (Volunteer v : allVolunteers())
            if (v.getId().equals(id))
                return v;
        return null;
    }

    public Volunteer findByEmail(String email) {
        for (Volunteer v : allVolunteers())
            if (v.getEmail().equalsIgnoreCase(email))
                return v;
        return null;
    }
}
