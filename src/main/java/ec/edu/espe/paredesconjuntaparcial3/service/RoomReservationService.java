package ec.edu.espe.paredesconjuntaparcial3.service;

import ec.edu.espe.paredesconjuntaparcial3.dto.RoomReservationResponse;
import ec.edu.espe.paredesconjuntaparcial3.model.RoomReservation;
import ec.edu.espe.paredesconjuntaparcial3.repository.RoomReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RoomReservationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private final RoomReservationRepository repository;
    private final UserPolicyClient userPolicyClient;

    public RoomReservationService(RoomReservationRepository repository, UserPolicyClient userPolicyClient) {
        this.repository = repository;
        this.userPolicyClient = userPolicyClient;
    }

    public RoomReservationResponse createReservation(String roomCode, String email, int hours) {
        // Validacion: roomCode no puede ser nulo ni vacio
        if (roomCode == null || roomCode.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de sala no puede ser nulo ni vacio");
        }

        // Validacion: email debe tener un formato valido
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("El email debe tener un formato valido");
        }

        // Validacion: hours debe ser mayor a 0 y menor o igual a 8
        if (hours <= 0 || hours > 8) {
            throw new IllegalArgumentException("Las horas deben ser mayor a 0 y menor o igual a 8");
        }

        // Validacion: No se puede crear una reserva si la sala ya se encuentra reservada
        if (repository.existsActiveReservationByRoomCode(roomCode)) {
            throw new IllegalStateException("La sala ya se encuentra reservada");
        }

        // Validacion: No se permite crear reservas para usuarios bloqueados por politicas institucionales
        if (userPolicyClient.isUserBlocked(email)) {
            throw new IllegalStateException("El usuario esta bloqueado por politicas institucionales");
        }

        RoomReservation reservation = new RoomReservation(roomCode, email, hours);
        RoomReservation saved = repository.save(reservation);
        return mapToResponse(saved);
    }

    public Optional<RoomReservationResponse> getReservationById(String id) {
        return repository.findById(id).map(this::mapToResponse);
    }

    public List<RoomReservationResponse> getAllReservations() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RoomReservationResponse> getReservationsByRoomCode(String roomCode) {
        return repository.findByRoomCode(roomCode).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RoomReservationResponse> getReservationsByEmail(String email) {
        return repository.findByReservedByEmail(email).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<RoomReservationResponse> confirmReservation(String id) {
        Optional<RoomReservation> reservationOpt = repository.findById(id);
        if (reservationOpt.isPresent()) {
            RoomReservation reservation = reservationOpt.get();
            reservation.setStatus(RoomReservation.ReservationStatus.CONFIRMED);
            RoomReservation saved = repository.save(reservation);
            return Optional.of(mapToResponse(saved));
        }
        return Optional.empty();
    }

    public void deleteReservation(String id) {
        repository.deleteById(id);
    }

    private RoomReservationResponse mapToResponse(RoomReservation reservation) {
        return new RoomReservationResponse(
                reservation.getId(),
                reservation.getRoomCode(),
                reservation.getReservedByEmail(),
                reservation.getHours(),
                reservation.getStatus().name()
        );
    }
}
