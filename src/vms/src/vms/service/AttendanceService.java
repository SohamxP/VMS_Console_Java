package src.vms.service;

import java.time.*;
import java.util.*;

import src.vms.model.*;

public class AttendanceService {
    private final DataStore db;

    public AttendanceService(DataStore db) {
        this.db = db;
    }

    // ---------- CHECK-IN ----------
    public Attendance checkIn(String eventId, String volunteerId) {
        String now = LocalDateTime.now().toString();
        return db.addCheckIn(eventId, volunteerId, now);
    }

    // ---------- CHECK-OUT ----------
    public Attendance checkOut(String attendanceId) {

        List<Attendance> list = db.loadAttendance();

        for (Attendance a : list) {
            if (a.getId().equals(attendanceId)) {

                LocalDateTime start = LocalDateTime.parse(a.getCheckIn());
                LocalDateTime end = LocalDateTime.now();

                a.setCheckOut(end.toString());

                Duration d = Duration.between(start, end);
                long minutes = d.toMinutes();
                long hrs = minutes / 60;
                long mins = minutes % 60;

                String worked = hrs + " hours " + mins + " minutes";
                a.setTimeWorked(worked);

                db.saveAttendance(list);
                return a;
            }
        }
        return null;
    }

    // ---------- SIGNUPS ----------
    public Signup signup(String eventId, String volunteerId) {
        List<Signup> list = db.loadSignups();

        for (Signup s : list) {
            if (s.getEventId().equals(eventId) && s.getVolunteerId().equals(volunteerId))
                return null; // already signed up
        }
        return db.addSignup(eventId, volunteerId);
    }

    public List<Signup> signupsForEvent(String eventId) {
        List<Signup> out = new ArrayList<>();
        for (Signup s : db.loadSignups())
            if (s.getEventId().equals(eventId))
                out.add(s);
        return out;
    }

    public boolean approveSignup(String signupId) {
        List<Signup> list = db.loadSignups();
        boolean ok = false;

        for (Signup s : list) {
            if (s.getId().equals(signupId)) {
                s.setStatus("APPROVED");
                ok = true;
            }
        }

        if (ok)
            db.saveSignups(list);
        return ok;
    }

    // ---------- REPORTS ----------
    public Map<String, String> detailedHoursByVolunteer() {
        Map<String, String> map = new HashMap<>();
        for (Attendance a : db.loadAttendance()) {
            map.put(a.getVolunteerId(), a.getTimeWorked());
        }
        return map;
    }

    public List<Attendance> attendanceForEvent(String eventId) {
        List<Attendance> out = new ArrayList<>();
        for (Attendance a : db.loadAttendance())
            if (a.getEventId().equals(eventId))
                out.add(a);
        return out;
    }
}
