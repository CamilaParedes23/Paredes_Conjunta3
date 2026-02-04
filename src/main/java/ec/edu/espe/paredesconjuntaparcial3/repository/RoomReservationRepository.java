package ec.edu.espe.paredesconjuntaparcial3.repository;

import ec.edu.espe.paredesconjuntaparcial3.model.RoomReservation;

import java.util.List;
import java.util.Optional;

public interface RoomReservationRepository {
    RoomReservation save(RoomReservation reservation);
    Optional<RoomReservation> findById(String id);
    List<RoomReservation> findAll();
    List<RoomReservation> findByRoomCode(String roomCode);
    List<RoomReservation> findByReservedByEmail(String email);
    void deleteById(String id);
    boolean existsActiveReservationByRoomCode(String roomCode);
}
