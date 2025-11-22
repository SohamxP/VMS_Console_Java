package vms.service;

import vms.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages volunteer signups, approvals, attendance, and hour reports.
 * Uses plain text storage handled by DataStore.
 */
public class AttendanceService {
    private final DataStore db;

    public AttendanceService(DataStore db) {
        this.db = db;
    }

    // Volunteer signs up for an event
    public Signup signup(String eventId, String volunteerId) {
        // Find the event
        Event e = null;
        for (Event ev : db.loadEvents()) {
            if (ev.getId().equals(eventId)) {
                e = ev;
                break;
            }
        }
        if (e == null) return null; // event not found

        // Check capacity
        List<Signup> list = db.loadSignups();
        long count = list.stream()
                .filter(s -> s.getEventId().equals(eventId))
                .count();
        if (count >= e.getCapacity()) return null; // event full

        // Add signup record
        return db.addSignup(eventId, volunteerId);
    }

    // View all signups for a given event
    public List<Signup> signupsForEvent(String eventId) {
        List<Signup> out = new ArrayList<>();
        for (Signup s : db.loadSignups()) {
            if (s.getEventId().equals(eventId)) {
                out.add(s);
            }
        }
        return out;
    }

    // Approve signup (admin action)
    public boolean approveSignup(String signupId) {
        List<Signup> list = db.loadSignups();
        boolean changed = false;
        for (Signup s : list) {
            if (s.getId().equals(signupId)) {
                s.setStatus("APPROVED");
                changed = true;
                break;
            }
        }
        if (changed) db.saveSignups(list);
        return changed;
    }

    // Record volunteer check-in (attendance)
    public Attendance checkIn(String eventId, String volunteerId, int hours) {
        return db.addAttendance(eventId, volunteerId, hours);
    }

    // Report: total credited hours per volunteer
    public Map<String, Integer> hoursByVolunteer() {
        Map<String, Integer> map = new HashMap<>();
        for (Attendance a : db.loadAttendance()) {
            map.put(
                a.getVolunteerId(),
                map.getOrDefault(a.getVolunteerId(), 0) + a.getHoursCredited()
            );
        }
        return map;
    }

    // Report: list of attendance records for an event
    public List<Attendance> attendanceForEvent(String eventId) {
        List<Attendance> out = new ArrayList<>();
        for (Attendance a : db.loadAttendance()) {
            if (a.getEventId().equals(eventId)) {
                out.add(a);
            }
        }
        return out;
    }
}
