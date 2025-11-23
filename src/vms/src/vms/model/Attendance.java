package src.vms.model;

public class Attendance {
    private String id;
    private String eventId;
    private String volunteerId;
    private String checkIn;
    private String checkOut;
    private String timeWorked; // "X hours Y minutes"

    public Attendance(String id, String eventId, String volunteerId,
            String checkIn, String checkOut, String timeWorked) {
        this.id = id;
        this.eventId = eventId;
        this.volunteerId = volunteerId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.timeWorked = timeWorked;
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public String getTimeWorked() {
        return timeWorked;
    }

    public void setCheckOut(String t) {
        this.checkOut = t;
    }

    public void setTimeWorked(String t) {
        this.timeWorked = t;
    }
}
