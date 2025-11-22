package vms.model;

public class Attendance {
    private String id, eventId, volunteerId, checkIn;
    private int hoursCredited;

    public Attendance(String id, String eventId, String volunteerId, String checkIn, int hoursCredited) {
        this.id = id;
        this.eventId = eventId;
        this.volunteerId = volunteerId;
        this.checkIn = checkIn;
        this.hoursCredited = hoursCredited;
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

    public int getHoursCredited() {
        return hoursCredited;
    }
}
