package vms.service;

import vms.model.*;
import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Handles file storage using plain text files (.txt) instead of CSV.
 * Each line is a record, values are comma-separated.
 */
public class DataStore {
    private final File usersFile;
    private final File eventsFile;
    private final File signupsFile;
    private final File attendanceFile;

    public DataStore(String dataDir) {
        this.usersFile = new File(dataDir, "users.txt");
        this.eventsFile = new File(dataDir, "events.txt");
        this.signupsFile = new File(dataDir, "signups.txt");
        this.attendanceFile = new File(dataDir, "attendance.txt");
        initDefaults();
    }

    private void initDefaults() {
        try {
            new File(usersFile.getParent()).mkdirs();
        } catch (Exception ignored) {
        }
        if (!usersFile.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(usersFile))) {
                // id,name,email,passwordHash,role
                pw.println("U1,Admin,admin@vms.local," + sha256("admin123") + ",ADMIN");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            if (!eventsFile.exists())
                eventsFile.createNewFile();
        } catch (IOException ignored) {
        }
        try {
            if (!signupsFile.exists())
                signupsFile.createNewFile();
        } catch (IOException ignored) {
        }
        try {
            if (!attendanceFile.exists())
                attendanceFile.createNewFile();
        } catch (IOException ignored) {
        }
    }

    // --- Utility methods ---
    public static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte x : b)
                sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String nextId(String prefix) {
        return prefix + UUID.randomUUID().toString().substring(0, 8);
    }

    private static List<String> readLines(File file) {
        List<String> lines = new ArrayList<>();
        if (!file.exists())
            return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty())
                    lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    private static void writeLines(File file, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
            for (String l : lines)
                pw.println(l);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ===== USERS =====
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        for (String line : readLines(usersFile)) {
            String[] r = line.split(",", -1);
            if (r.length < 5)
                continue;
            String id = r[0], name = r[1], email = r[2], ph = r[3], role = r[4];
            if ("ADMIN".equals(role))
                users.add(new Admin(id, name, email, ph));
            else
                users.add(new Volunteer(id, name, email, ph));
        }
        return users;
    }

    public void saveUsers(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(String.join(",", u.getId(), u.getName(), u.getEmail(), u.getPasswordHash(), u.getRole()));
        }
        writeLines(usersFile, lines);
    }

    public User addVolunteer(String name, String email, String password) {
        List<User> users = loadUsers();
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(email))
                return null;
        String id = nextId("U");
        Volunteer v = new Volunteer(id, name, email, sha256(password));
        users.add(v);
        saveUsers(users);
        return v;
    }

    // ===== EVENTS =====
    public List<Event> loadEvents() {
        List<Event> list = new ArrayList<>();
        for (String line : readLines(eventsFile)) {
            String[] r = line.split(",", -1);
            if (r.length < 7)
                continue;
            list.add(new Event(r[0], r[1], r[2], r[3], r[4],
                    Integer.parseInt(r[5]), Integer.parseInt(r[6])));
        }
        return list;
    }

    public void saveEvents(List<Event> events) {
        List<String> lines = new ArrayList<>();
        for (Event e : events) {
            lines.add(String.join(",", e.getId(), e.getTitle(), e.getDate(), e.getTime(),
                    e.getLocation(), String.valueOf(e.getCapacity()), String.valueOf(e.getDurationHrs())));
        }
        writeLines(eventsFile, lines);
    }

    public Event addEvent(String title, String date, String time, String location, int cap, int dur) {
        List<Event> events = loadEvents();
        Event e = new Event(nextId("E"), title, date, time, location, cap, dur);
        events.add(e);
        saveEvents(events);
        return e;
    }

    // ===== SIGNUPS =====
    public List<Signup> loadSignups() {
        List<Signup> list = new ArrayList<>();
        for (String line : readLines(signupsFile)) {
            String[] r = line.split(",", -1);
            if (r.length < 4)
                continue;
            list.add(new Signup(r[0], r[1], r[2], r[3]));
        }
        return list;
    }

    public void saveSignups(List<Signup> list) {
        List<String> lines = new ArrayList<>();
        for (Signup s : list) {
            lines.add(String.join(",", s.getId(), s.getEventId(), s.getVolunteerId(), s.getStatus()));
        }
        writeLines(signupsFile, lines);
    }

    public Signup addSignup(String eventId, String volunteerId) {
        List<Signup> list = loadSignups();
        for (Signup s : list)
            if (s.getEventId().equals(eventId) && s.getVolunteerId().equals(volunteerId))
                return null;
        Signup s = new Signup(nextId("S"), eventId, volunteerId, "PENDING");
        list.add(s);
        saveSignups(list);
        return s;
    }

    // ===== ATTENDANCE =====
    public List<Attendance> loadAttendance() {
        List<Attendance> list = new ArrayList<>();
        for (String line : readLines(attendanceFile)) {
            String[] r = line.split(",", -1);
            if (r.length < 5)
                continue;
            list.add(new Attendance(r[0], r[1], r[2], r[3], Integer.parseInt(r[4])));
        }
        return list;
    }

    public void saveAttendance(List<Attendance> list) {
        List<String> lines = new ArrayList<>();
        for (Attendance a : list) {
            lines.add(String.join(",", a.getId(), a.getEventId(), a.getVolunteerId(),
                    a.getCheckIn(), String.valueOf(a.getHoursCredited())));
        }
        writeLines(attendanceFile, lines);
    }

    public Attendance addAttendance(String eventId, String volunteerId, int hoursCredited) {
        List<Attendance> list = loadAttendance();
        Attendance a = new Attendance(nextId("A"), eventId, volunteerId,
                java.time.LocalDateTime.now().toString(), hoursCredited);
        list.add(a);
        saveAttendance(list);
        return a;
    }
}
