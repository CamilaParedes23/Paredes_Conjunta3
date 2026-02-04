package ec.edu.espe.paredesconjuntaparcial3.service;

import ec.edu.espe.paredesconjuntaparcial3.dto.RoomReservationResponse;
import ec.edu.espe.paredesconjuntaparcial3.model.RoomReservation;
import ec.edu.espe.paredesconjuntaparcial3.repository.RoomReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomReservationServiceTest {

    @Mock
    private RoomReservationRepository repository;

    @Mock
    private UserPolicyClient userPolicyClient;

    private RoomReservationService service;

    @BeforeEach
    void setUp() {
        service = new RoomReservationService(repository, userPolicyClient);
    }

    @Test
    @DisplayName("Debe crear una reserva exitosamente con datos validos")
    void createReservation_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "camila@espe.edu.ec";
        int hours = 4;

        when(repository.existsActiveReservationByRoomCode(roomCode)).thenReturn(false);
        when(userPolicyClient.isUserBlocked(email)).thenReturn(false);
        when(repository.save(any(RoomReservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RoomReservationResponse response = service.createReservation(roomCode, email, hours);

        // Assert
        assertNotNull(response);
        assertEquals(roomCode, response.getRoomCode());
        assertEquals(email, response.getReservedByEmail());
        assertEquals(hours, response.getHours());
        assertEquals("CREATED", response.getStatus());

        verify(repository).existsActiveReservationByRoomCode(roomCode);
        verify(userPolicyClient).isUserBlocked(email);
        verify(repository).save(any(RoomReservation.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando roomCode es nulo")
    void createReservation_WithNullRoomCode_ShouldThrowException() {
        // Arrange
        String email = "camila@espe.edu.ec";
        int hours = 4;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(null, email, hours));

        assertEquals("El codigo de sala no puede ser nulo ni vacio", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando roomCode esta vacio")
    void createReservation_WithEmptyRoomCode_ShouldThrowException() {
        // Arrange
        String email = "estudiante@espe.edu.ec";
        int hours = 4;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation("", email, hours));

        assertEquals("El codigo de sala no puede ser nulo ni vacio", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando roomCode solo tiene espacios en blanco")
    void createReservation_WithBlankRoomCode_ShouldThrowException() {
        // Arrange
        String email = "estudiante@espe.edu.ec";
        int hours = 4;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation("   ", email, hours));

        assertEquals("El codigo de sala no puede ser nulo ni vacio", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando email es nulo")
    void createReservation_WithNullEmail_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        int hours = 4;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(roomCode, null, hours));

        assertEquals("El email debe tener un formato valido", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando email tiene formato invalido")
    void createReservation_WithInvalidEmail_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        String invalidEmail = "correo-invalido";
        int hours = 4;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(roomCode, invalidEmail, hours));

        assertEquals("El email debe tener un formato valido", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando hours es 0")
    void createReservation_WithZeroHours_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "estudiante@espe.edu.ec";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(roomCode, email, 0));

        assertEquals("Las horas deben ser mayor a 0 y menor o igual a 8", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando hours es negativo")
    void createReservation_WithNegativeHours_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "estudiante@espe.edu.ec";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(roomCode, email, -1));

        assertEquals("Las horas deben ser mayor a 0 y menor o igual a 8", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando hours es mayor a 8")
    void createReservation_WithMoreThan8Hours_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "estudiante@espe.edu.ec";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(roomCode, email, 9));

        assertEquals("Las horas deben ser mayor a 0 y menor o igual a 8", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando la sala ya esta reservada")
    void createReservation_WithAlreadyReservedRoom_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "estudiante@espe.edu.ec";
        int hours = 4;

        when(repository.existsActiveReservationByRoomCode(roomCode)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.createReservation(roomCode, email, hours));

        assertEquals("La sala ya se encuentra reservada", exception.getMessage());
        verify(repository).existsActiveReservationByRoomCode(roomCode);
        verifyNoInteractions(userPolicyClient);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el usuario esta bloqueado por politicas institucionales")
    void createReservation_WithBlockedUser_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "usuario.bloqueado@espe.edu.ec";
        int hours = 4;

        when(repository.existsActiveReservationByRoomCode(roomCode)).thenReturn(false);
        when(userPolicyClient.isUserBlocked(email)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.createReservation(roomCode, email, hours));

        assertEquals("El usuario esta bloqueado por politicas institucionales", exception.getMessage());
        verify(repository).existsActiveReservationByRoomCode(roomCode);
        verify(userPolicyClient).isUserBlocked(email);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debe crear reserva con exactamente 8 horas")
    void createReservation_With8Hours_ShouldCreateSuccessfully() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "estudiante@espe.edu.ec";
        int hours = 8;

        when(repository.existsActiveReservationByRoomCode(roomCode)).thenReturn(false);
        when(userPolicyClient.isUserBlocked(email)).thenReturn(false);
        when(repository.save(any(RoomReservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RoomReservationResponse response = service.createReservation(roomCode, email, hours);

        // Assert
        assertNotNull(response);
        assertEquals(hours, response.getHours());
        verify(repository).save(any(RoomReservation.class));
    }

    @Test
    @DisplayName("Debe crear reserva con exactamente 1 hora")
    void createReservation_With1Hour_ShouldCreateSuccessfully() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "estudiante@espe.edu.ec";
        int hours = 1;

        when(repository.existsActiveReservationByRoomCode(roomCode)).thenReturn(false);
        when(userPolicyClient.isUserBlocked(email)).thenReturn(false);
        when(repository.save(any(RoomReservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RoomReservationResponse response = service.createReservation(roomCode, email, hours);

        // Assert
        assertNotNull(response);
        assertEquals(hours, response.getHours());
        verify(repository).save(any(RoomReservation.class));
    }

    @Test
    @DisplayName("Las validaciones deben ejecutarse antes de interactuar con dependencias externas")
    void createReservation_ValidationOrder_ShouldValidateBeforeExternalCalls() {
        // Arrange - datos invalidos
        String roomCode = "";
        String email = "estudiante@espe.edu.ec";
        int hours = 4;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(roomCode, email, hours));

        // Verificar que no se llamaron las dependencias externas
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }
}

