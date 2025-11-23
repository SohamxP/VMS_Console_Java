package src.vms;

import java.util.*;

import src.vms.model.*;
import src.vms.service.*;

import java.io.File;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static DataStore db;
    private static AuthService auth;
    private static EventService events;
    private static VolunteerService vols;
    private static AttendanceService attend;

    public static void main(String[] args) {

        String dataDir = System.getProperty("vms.dataDir", "data");
        File dir = new File(dataDir);
        if (!dir.exists())
            dir.mkdirs();

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
            String c = sc.nextLine().trim();

            switch (c) {
                case "1":
                    doLogin();
                    break;
                case "2":
                    doRegister();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid.");
            }
        }
    }

    private static void doRegister() {
        System.out.println("\n-- Volunteer Registration --");
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String pw = sc.nextLine();

        Volunteer v = vols.register(name, email, pw);
        System.out.println(v == null ? "Email exists!" : "Registered!");
    }

    private static void doLogin() {
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String pw = sc.nextLine();

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

    // ---------------- ADMIN MENU ----------------
    private static void adminMenu(User admin) {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1) Create event");
            System.out.println("2) List events");
            System.out.println("3) View event signups");
            System.out.println("4) Approve signup");
            System.out.println("5) Check-in volunteer");
            System.out.println("6) Check-out volunteer");
            System.out.println("7) Reports");
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
                case "5":
                    adminCheckIn();
                    break;
                case "6":
                    adminCheckOut();
                    break;
                case "7":
                    adminReports();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid.");
            }
        }
    }

    private static void adminCreateEvent() {
        System.out.println("\n-- Create Event --");
        System.out.print("Title: ");
        String t = sc.nextLine();
        System.out.print("Date: ");
        String d = sc.nextLine();
        System.out.print("Time: ");
        String ti = sc.nextLine();
        System.out.print("Location: ");
        String loc = sc.nextLine();
        System.out.print("Capacity: ");
        int cap = Integer.parseInt(sc.nextLine());
        System.out.print("Duration (hrs): ");
        int dur = Integer.parseInt(sc.nextLine());

        Event e = events.create(t, d, ti, loc, cap, dur);
        System.out.println("Created: " + e);
    }

    private static void listEvents() {
        System.out.println("\n-- Events --");
        for (Event e : events.all())
            System.out.println(e);
    }

    private static void adminViewSignups() {
        System.out.print("Event ID: ");
        String eid = sc.nextLine();
        List<Signup> list = attend.signupsForEvent(eid);

        if (list.isEmpty())
            System.out.println("No signups.");
        else
            for (Signup s : list)
                System.out.println(s.getId() + " | " + s.getVolunteerId() + " | " + s.getStatus());
    }

    private static void adminApproveSignup() {
        System.out.print("Signup ID: ");
        String sid = sc.nextLine();
        boolean ok = attend.approveSignup(sid);
        System.out.println(ok ? "Approved!" : "Not found!");
    }

    private static void adminCheckIn() {
        System.out.print("Event ID: ");
        String eid = sc.nextLine();
        System.out.print("Volunteer ID: ");
        String vid = sc.nextLine();

        Attendance a = attend.checkIn(eid, vid);
        System.out.println("Checked in! Attendance ID = " + a.getId());
    }

    private static void adminCheckOut() {
        System.out.print("Attendance ID: ");
        String aid = sc.nextLine();

        Attendance a = attend.checkOut(aid);
        if (a == null)
            System.out.println("Invalid ID.");
        else {
            System.out.println("Checked out!");
            System.out.println("Time worked = " + a.getTimeWorked());
        }
    }

    private static void adminReports() {
        System.out.println("\n-- Reports --");
        System.out.println("Volunteer Hours:");
        Map<String, String> map = attend.detailedHoursByVolunteer();

        for (String vid : map.keySet()) {
            Volunteer v = vols.findById(vid);
            String name = (v != null ? v.getName() : vid);
            System.out.println(name + ": " + map.get(vid));
        }
    }

    // ---------------- VOLUNTEER MENU ----------------
    private static void volunteerMenu(Volunteer v) {
        while (true) {
            System.out.println("\n=== Volunteer Menu ===");
            System.out.println("1) List events");
            System.out.println("2) Search events");
            System.out.println("3) Sign up for event");
            System.out.println("4) View credited hours");
            System.out.println("0) Logout");
            System.out.print("Choose: ");

            String c = sc.nextLine();

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
            }
        }
    }

    private static void searchEvents() {
        System.out.print("Search: ");
        String q = sc.nextLine();
        List<Event> list = events.searchByTitle(q);

        if (list.isEmpty())
            System.out.println("No matches.");
        else
            for (Event e : list)
                System.out.println(e);
    }

    private static void volunteerSignup(Volunteer v) {
        System.out.print("Event ID: ");
        String eid = sc.nextLine();
        Signup s = attend.signup(eid, v.getId());

        System.out.println(s == null ? "Signup failed." : "Signup complete! ID = " + s.getId());
    }

    private static void volunteerHours(Volunteer v) {
        System.out.println("Your hours: ");

        Map<String, String> map = attend.detailedHoursByVolunteer();
        System.out.println(map.getOrDefault(v.getId(), "0 hours 0 minutes"));
    }
}
