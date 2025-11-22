package vms.model;

public class Signup {
    private String id, eventId, volunteerId, status;

    public Signup(String id, String eventId, String volunteerId, String status) {
        this.id = id;
        this.eventId = eventId;
        this.volunteerId = volunteerId;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
