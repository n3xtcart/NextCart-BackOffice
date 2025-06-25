package org.example.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@DisplayName("StartupService Tests")
public class StartupServiceTest {


    @QuarkusTest
    @TestProfile(DevProfile.class)
    @DisplayName("in 'dev' profile")
    public static class DevModeTest {


        @InjectMock
        ServizioAutenticazione servizioAutenticazione;

        @Test
        @DisplayName("should call the default admin initialization")
        void testOnStart_inDevMode_callsInitialization() {

            verify(servizioAutenticazione, times(1))
                    .inizializzaUtenteAdminSeAssente("admin@example.com", "password", "admin");
        }
    }

    @QuarkusTest
    @TestProfile(ProdProfile.class)
    @DisplayName("in 'prod' profile")
    public static class ProdModeTest {

        @InjectMock
        ServizioAutenticazione servizioAutenticazione;

        @Test
        @DisplayName("should NOT call the default admin initialization")
        void testOnStart_inProdMode_doesNotCallInitialization() {
            verify(servizioAutenticazione, never())
                    .inizializzaUtenteAdminSeAssente(anyString(), anyString(), anyString());
        }
    }

    @QuarkusTest
    @TestProfile(DevProfile.class)
    @DisplayName("with exception during startup in 'dev' profile")
    public static class ExceptionHandlingTest {

        @InjectMock
        ServizioAutenticazione servizioAutenticazione;


        @BeforeEach
        void setupMockToThrowException() {

            doThrow(new RuntimeException("Simulated Database Connection Error"))
                    .when(servizioAutenticazione)
                    .inizializzaUtenteAdminSeAssente(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("should catch exception and not crash the application")
        void onStart_shouldHandleException() {

            verify(servizioAutenticazione, times(1))
                    .inizializzaUtenteAdminSeAssente(anyString(), anyString(), anyString());
        }
    }





    public static class DevProfile implements io.quarkus.test.junit.QuarkusTestProfile {
        @Override
        public String getConfigProfile() {
            return "dev";
        }
    }


    public static class ProdProfile implements io.quarkus.test.junit.QuarkusTestProfile {
        @Override
        public String getConfigProfile() {
            return "prod";
        }
    }
}