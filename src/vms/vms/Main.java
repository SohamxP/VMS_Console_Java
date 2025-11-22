package vms;

import vms.model.*;
import vms.service.*;
import java.util.*;
import java.io.File;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static DataStore db;
    private static AuthService auth;
    private static EventService events;
    private static VolunteerService vols;
    private static AttendanceService attend;

    public static void main(String[] args) {
        // ✅ Ensure data directory exists
        String dataDir = System.getProperty("vms.dataDir", "data");
        File dir = new File(dataDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created)
                System.out.println("[Info] Created data folder: " + dir.getAbsolutePath());
        }

        db = new DataStore(dataDir);
        auth = new AuthService(db);
        events = new EventService(db);
        vols = new VolunteerService(db);
        attend = new AttendanceService(db);

        while (true) {
            System.out.println("\n=== Volunteer Management System ===");
            System.out.println("1) Login");
            System.out.println("2) Register (Volunteer)");
            System.out.println("0) Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    doLogin();
                    break;
                case "2":
                    doRegister();
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid.");
                    break;
            }
        }
    }

    private static void doRegister() {
        System.out.println("\n-- Volunteer Registration --");
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String pw = sc.nextLine().trim();
        Volunteer v = vols.register(name, email, pw);
        if (v == null)
            System.out.println("Email already exists.");
        else
            System.out.println("Registered! You can now login.");
    }

    private static void doLogin() {
        System.out.print("\nEmail: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String pw = sc.nextLine().trim();
        User u = auth.login(email, pw);
        if (u == null) {
            System.out.println("Login failed.");
            return;
        }
        if ("ADMIN".equals(u.getRole()))
            adminMenu(u);
        else
            volunteerMenu((Volunteer) u);
    }

    // ========== ADMIN MENU ==========
    private static void adminMenu(User admin) {
        while (true) {
            System.out.println("\n=== Admin Menu (" + admin.getName() + ") ===");
            System.out.println("1) Create event");
            System.out.println("2) List events");
            System.out.println("3) View signups for event");
            System.out.println("4) Approve a signup");
            System.out.println("0) Logout");
            System.out.print("Choose: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1":
                    adminCreateEvent();
                    break;
                case "2":
                    listEvents();
                    break;
                case "3":
                    adminViewSignups();
                    break;
                case "4":
                    adminApproveSignup();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid.");
                    break;
            }
        }
    }

    private static void adminCreateEvent() {
        System.out.println("\n-- Create Event --");
        System.out.print("Title: ");
        String title = sc.nextLine().trim();
        System.out.print("Date (yyyy-MM-dd): ");
        String date = sc.nextLine().trim();
        System.out.print("Time (HH:mm): ");
        String time = sc.nextLine().trim();
        System.out.print("Location: ");
        String location = sc.nextLine().trim();
        System.out.print("Capacity: ");
        int cap = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Duration (hours): ");
        int dur = Integer.parseInt(sc.nextLine().trim());
        Event e = events.create(title, date, time, location, cap, dur);
        System.out.println("Created: " + e);
    }

    private static void listEvents() {
        System.out.println("\n-- Events --");
        for (Event e : events.all())
            System.out.println(e);
    }

    private static void adminViewSignups() {
        System.out.print("Event ID: ");
        String eid = sc.nextLine().trim();
        List<Signup> list = attend.signupsForEvent(eid);
        if (list.isEmpty())
            System.out.println("No signups.");
        else
            for (Signup s : list)
                System.out.println(s.getId() + " :: volunteer=" + s.getVolunteerId() + " :: " + s.getStatus());
    }

    private static void adminApproveSignup() {
        System.out.print("Signup ID to approve: ");
        String sid = sc.nextLine().trim();
        boolean ok = attend.approveSignup(sid);
        System.out.println(ok ? "Approved." : "Not found.");
    }

    private static void volunteerMenu(Volunteer v) {
        while (true) {
            System.out.println("\n=== Volunteer Menu (" + v.getName() + ") ===");
            System.out.println("1) List events");
            System.out.println("2) Search events");
            System.out.println("3) Sign up for event");
            System.out.println("4) View credited hours");
            System.out.println("0) Logout");
            System.out.print("Choose: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1":
                    listEvents();
                    break;
                case "2":
                    searchEvents();
                    break;
                case "3":
                    volunteerSignup(v);
                    break;
                case "4":
                    volunteerHours(v);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid.");
                    break;
            }
        }
    }

    private static void searchEvents() {
        System.out.print("Search by title: ");
        String q = sc.nextLine().trim();
        List<Event> list = events.searchByTitle(q);
        if (list.isEmpty())
            System.out.println("No matches.");
        else
            for (Event e : list)
                System.out.println(e);
    }

    private static void volunteerSignup(Volunteer v) {
        System.out.print("Event ID to sign up: ");
        String eid = sc.nextLine().trim();
        Signup s = attend.signup(eid, v.getId());
        if (s == null)
            System.out.println("Could not sign up (maybe full or already signed).");
        else
            System.out.println("Signup requested: " + s.getId());
    }

    private static void volunteerHours(Volunteer v) {
        Map<String, Integer> map = attend.hoursByVolunteer();
        int hrs = map.getOrDefault(v.getId(), 0);
        System.out.println("You have " + hrs + " credited hours.");
    }
}
