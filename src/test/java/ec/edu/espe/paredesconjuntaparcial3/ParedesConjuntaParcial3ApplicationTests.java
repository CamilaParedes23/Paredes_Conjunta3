package ec.edu.espe.paredesconjuntaparcial3;

import ec.edu.espe.paredesconjuntaparcial3.dto.RoomReservationResponse;
import ec.edu.espe.paredesconjuntaparcial3.model.RoomReservation;
import ec.edu.espe.paredesconjuntaparcial3.repository.RoomReservationRepository;
import ec.edu.espe.paredesconjuntaparcial3.service.RoomReservationService;
import ec.edu.espe.paredesconjuntaparcial3.service.UserPolicyClient;
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
class ParedesConjuntaParcial3ApplicationTests {
    @Mock
    private RoomReservationRepository repository;

    @Mock
    private UserPolicyClient userPolicyClient;

    private RoomReservationService service;

    //Before
    @BeforeEach
    void setUp() {

        service = new RoomReservationService(repository, userPolicyClient);
    }


    //Prueba 1
    //Creación exitosa
    @Test
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

    //Prueba 2
    //correo inválido
    @Test
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


    //Prueba 2
    //Correo inválido
    @Test
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

    //Prueba 3
    //Horas fuera del servicio
    @Test
    @DisplayName("Debe lanzar excepcion cuando hours es 0")
    void createReservation_WithZeroHours_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "camila@espe.edu.ec";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(roomCode, email, 0));

        assertEquals("Las horas deben ser mayor a 0 y menor o igual a 8", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }


    //Prueba 3
    //Mayor a 8 horas
    @Test
    void createReservation_WithMoreThan8Hours_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "camila@espe.edu.ec";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(roomCode, email, 9));

        assertEquals("Las horas deben ser mayor a 0 y menor o igual a 8", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(userPolicyClient);
    }

    //Prueba 4
    //Sala ya reservada
    @Test
    void createReservation_WithAlreadyReservedRoom_ShouldThrowException() {
        // Arrange
        String roomCode = "LAB-101";
        String email = "camila@espe.edu.ec";
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
    }
