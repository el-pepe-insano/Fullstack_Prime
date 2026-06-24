package com.nopki.licencias.config;

import com.nopki.licencias.model.EstadoLicencia;
import com.nopki.licencias.model.Licencia;
import com.nopki.licencias.repository.LicenciaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final LicenciaRepository licenciaRepository;

    @Override
    public void run(String... args) {
        if (licenciaRepository.count() == 0) {
            log.info("Cargando licencias iniciales...");

            String[][] licencias = {
                // Resident Evil (juegoId 1)
                {"1", "NOPKI-RE01-A1B2-C3D4"},
                {"1", "NOPKI-RE01-B2C3-D4E5"},
                {"1", "NOPKI-RE01-C3D4-E5F6"},
                // Resident Evil 2 (juegoId 2)
                {"2", "NOPKI-RE02-D4E5-F6G7"},
                {"2", "NOPKI-RE02-E5F6-G7H8"},
                {"2", "NOPKI-RE02-F6G7-H8I9"},
                // Resident Evil 3 (juegoId 3)
                {"3", "NOPKI-RE03-G7H8-I9J0"},
                {"3", "NOPKI-RE03-H8I9-J0K1"},
                {"3", "NOPKI-RE03-I9J0-K1L2"},
                // Resident Evil 4 (juegoId 4)
                {"4", "NOPKI-RE04-J0K1-L2M3"},
                {"4", "NOPKI-RE04-K1L2-M3N4"},
                {"4", "NOPKI-RE04-L2M3-N4O5"},
                // Resident Evil 7 (juegoId 5)
                {"5", "NOPKI-RE07-M3N4-O5P6"},
                {"5", "NOPKI-RE07-N4O5-P6Q7"},
                {"5", "NOPKI-RE07-O5P6-Q7R8"},
                // Resident Evil Village (juegoId 6)
                {"6", "NOPKI-REV8-P6Q7-R8S9"},
                {"6", "NOPKI-REV8-Q7R8-S9T0"},
                {"6", "NOPKI-REV8-R8S9-T0U1"},
                // The Witcher 3 (juegoId 7)
                {"7", "NOPKI-TW03-S9T0-U1V2"},
                {"7", "NOPKI-TW03-T0U1-V2W3"},
                // Hollow Knight (juegoId 8)
                {"8", "NOPKI-HK08-U1V2-W3X4"},
                {"8", "NOPKI-HK08-V2W3-X4Y5"},
                // Hades (juegoId 9)
                {"9", "NOPKI-HD09-W3X4-Y5Z6"},
                {"9", "NOPKI-HD09-X4Y5-Z6A7"},
                // Stardew Valley (juegoId 10)
                {"10", "NOPKI-SDV0-Y5Z6-A7B8"},
                {"10", "NOPKI-SDV0-Z6A7-B8C9"}
            };

            for (String[] l : licencias) {
                licenciaRepository.save(Licencia.builder()
                        .juegoId(Long.parseLong(l[0]))
                        .codigo(l[1])
                        .estado(EstadoLicencia.DISPONIBLE)
                        .build());
            }

            log.info("✅ {} licencias cargadas", licenciaRepository.count());
        } else {
            log.info("Licencias ya existentes, omitiendo carga inicial");
        }
    }
}