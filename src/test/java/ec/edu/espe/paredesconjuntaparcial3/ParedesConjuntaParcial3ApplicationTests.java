package ec.edu.espe.paredesconjuntaparcial3;

import ec.edu.espe.paredesconjuntaparcial3.repository.RoomReservationRepository;
import ec.edu.espe.paredesconjuntaparcial3.service.RoomReservationService;
import ec.edu.espe.paredesconjuntaparcial3.service.UserPolicyClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
class ParedesConjuntaParcial3ApplicationTests {
    private RoomReservationRepository roomReservationRepository;
    private RoomReservationService roomReservationService;
    private UserPolicyClient userPolicyClient;

    //Before
    @BeforeEach
    public void setUp(){
        roomReservationRepository = Mockito.mock(RoomReservationRepository.class);
        userPolicyClient = Mockito.mock(UserPolicyClient.class);
        roomReservationService = Mockito.mock(RoomReservationService.class);
    }

    //Primera prueba creación exitosa
    @Test
    void successReservation() {
        //Arrange

        //Act

        //Assert
    }

    //Segunda prueba
    // error por correo electrónico inválido
    @Test
    void errorEmail(){

    }

    //Tercera prueba
    //Error por número de horas fuera del rango permitido
    @Test
    void errorHours(){

    }

    //Cuarta prueba
    //Error cuando la sala ya se encuentra reservada
    @Test
    void errorHallReservated(){

    }
}
