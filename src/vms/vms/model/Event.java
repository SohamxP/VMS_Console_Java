package vms.model;

public class Event {
    private String id, title, date, time, location;
    private int capacity, durationHrs;

    public Event(String id, String title, String date, String time, String location, int capacity, int durationHrs) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.capacity = capacity;
        this.durationHrs = durationHrs;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getDurationHrs() {
        return durationHrs;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " on " + date + " " + time + " at " + location +
                " (cap=" + capacity + ", " + durationHrs + "h)";
    }
}
