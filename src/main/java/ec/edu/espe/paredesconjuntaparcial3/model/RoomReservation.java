package ec.edu.espe.paredesconjuntaparcial3.model;

import java.util.UUID;

public class RoomReservation {
    private String id;
    private String roomCode;
    private String reservedByEmail;
    private int hours;
    private ReservationStatus status;

    public enum ReservationStatus {
        CREATED,
        CONFIRMED
    }

    public RoomReservation() {
        this.id = UUID.randomUUID().toString();
        this.status = ReservationStatus.CREATED;
    }

    public RoomReservation(String roomCode, String reservedByEmail, int hours) {
        this.id = UUID.randomUUID().toString();
        this.roomCode = roomCode;
        this.reservedByEmail = reservedByEmail;
        this.hours = hours;
        this.status = ReservationStatus.CREATED;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getReservedByEmail() {
        return reservedByEmail;
    }

    public void setReservedByEmail(String reservedByEmail) {
        this.reservedByEmail = reservedByEmail;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
