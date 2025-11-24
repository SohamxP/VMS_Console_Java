package src.vms.service;

import java.util.*;

import src.vms.model.*;

import java.io.*;
import java.security.MessageDigest;

public class DataStore {

    private final File usersFile;
    private final File eventsFile;
    private final File signupsFile;
    private final File attendanceFile;

    public DataStore(String dataDir) {
        usersFile = new File(dataDir, "users.txt");
        eventsFile = new File(dataDir, "events.txt");
        signupsFile = new File(dataDir, "signups.txt");
        attendanceFile = new File(dataDir, "attendance.txt");

        initDefaults();
    }

    private void initDefaults() {
        try {
            new File(usersFile.getParent()).mkdirs();
        } catch (Exception ignored) {
        }

        if (!usersFile.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(usersFile))) {
                pw.println("U1,Admin,admin@vms.local," + sha256("admin123") + ",ADMIN");
            } catch (Exception ignored) {
            }
        }

        try {
            eventsFile.createNewFile();
        } catch (Exception ignored) {
        }
        try {
            signupsFile.createNewFile();
        } catch (Exception ignored) {
        }
        try {
            attendanceFile.createNewFile();
        } catch (Exception ignored) {
        }
    }

    //Utility Method

    public static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte x : b)
                sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static String nextId(String prefix) {
        return prefix + UUID.randomUUID().toString().substring(0, 8);
    }

    private List<String> read(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null)
                if (!line.trim().isEmpty())
                    lines.add(line);
        } catch (Exception ignored) {
        }
        return lines;
    }

    private void write(File file, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String s : lines)
                pw.println(s);
        } catch (Exception ignored) {
        }
    }

    //USERS
    public List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        for (String line : read(usersFile)) {
            String[] r = line.split(",", -1);

            if (r[4].equals("ADMIN"))
                list.add(new Admin(r[0], r[1], r[2], r[3]));
            else
                list.add(new Volunteer(r[0], r[1], r[2], r[3]));
        }
        return list;
    }

    public void saveUsers(List<User> list) {
        List<String> out = new ArrayList<>();
        for (User u : list)
            out.add(String.join(",", u.getId(), u.getName(), u.getEmail(), u.getPasswordHash(), u.getRole()));
        write(usersFile, out);
    }

    public User addVolunteer(String name, String email, String password) {
        List<User> list = loadUsers();

        for (User u : list)
            if (u.getEmail().equalsIgnoreCase(email))
                return null;

        Volunteer v = new Volunteer(nextId("U"), name, email, sha256(password));
        list.add(v);
        saveUsers(list);

        return v;
    }

    //EVENTS
    public List<Event> loadEvents() {
        List<Event> list = new ArrayList<>();
        for (String line : read(eventsFile)) {
            String[] r = line.split(",", -1);

            list.add(new Event(
                    r[0], r[1], r[2], r[3], r[4],
                    Integer.parseInt(r[5]),
                    Integer.parseInt(r[6])));
        }
        return list;
    }

    public void saveEvents(List<Event> list) {
        List<String> out = new ArrayList<>();
        for (Event e : list) {
            out.add(String.join(",",
                    e.getId(),
                    e.getTitle(),
                    e.getDate(),
                    e.getTime(),
                    e.getLocation(),
                    "" + e.getCapacity(),
                    "" + e.getDurationHrs()));
        }
        write(eventsFile, out);
    }

    public Event addEvent(String t, String d, String ti, String loc, int cap, int dur) {
        List<Event> list = loadEvents();
        Event e = new Event(nextId("E"), t, d, ti, loc, cap, dur);
        list.add(e);
        saveEvents(list);
        return e;
    }

    //SIGN UPS
    public List<Signup> loadSignups() {
        List<Signup> list = new ArrayList<>();
        for (String line : read(signupsFile)) {
            String[] r = line.split(",", -1);
            list.add(new Signup(r[0], r[1], r[2], r[3]));
        }
        return list;
    }

    public void saveSignups(List<Signup> list) {
        List<String> out = new ArrayList<>();
        for (Signup s : list)
            out.add(String.join(",", s.getId(), s.getEventId(), s.getVolunteerId(), s.getStatus()));
        write(signupsFile, out);
    }

    public Signup addSignup(String eventId, String volunteerId) {
        List<Signup> list = loadSignups();

        Signup s = new Signup(nextId("S"), eventId, volunteerId, "PENDING");
        list.add(s);
        saveSignups(list);

        return s;
    }

    //ATTENDANCE
    public List<Attendance> loadAttendance() {
        List<Attendance> list = new ArrayList<>();

        for (String line : read(attendanceFile)) {
            String[] r = line.split(",", -1);

            list.add(new Attendance(
                    r[0], // ID
                    r[1], // Event ID
                    r[2], // Volunteer ID
                    r[3], // Check-in timestamp
                    r[4], // Check-out timestamp
                    r[5] // Hours + minutes worked
            ));
        }

        return list;
    }

    public void saveAttendance(List<Attendance> list) {
        List<String> out = new ArrayList<>();

        for (Attendance a : list) {
            out.add(String.join(",",
                    a.getId(),
                    a.getEventId(),
                    a.getVolunteerId(),
                    a.getCheckIn(),
                    a.getCheckOut(),
                    a.getTimeWorked()));
        }

        write(attendanceFile, out);
    }

    // Create New check-in entry
    public Attendance addCheckIn(String eventId, String volunteerId, String checkInTime) {
        List<Attendance> list = loadAttendance();

        Attendance a = new Attendance(
                nextId("A"),
                eventId,
                volunteerId,
                checkInTime,
                "",
                "");

        list.add(a);
        saveAttendance(list);

        return a;
    }
}
