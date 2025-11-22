package vms.service;

import vms.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class EventService {
    private final DataStore db;

    public EventService(DataStore db) {
        this.db = db;
    }

    public Event create(String title, String date, String time, String location, int cap, int dur) {
        return db.addEvent(title, date, time, location, cap, dur);
    }

    public List<Event> all() {
        return db.loadEvents();
    }

    public Event findById(String id) {
        for (Event e : db.loadEvents())
            if (e.getId().equals(id))
                return e;
        return null;
    }

    public List<Event> searchByTitle(String q) {
        String qq = q.toLowerCase();
        return db.loadEvents().stream()
                .filter(e -> e.getTitle().toLowerCase().contains(qq))
                .collect(Collectors.toList());
    }
}
