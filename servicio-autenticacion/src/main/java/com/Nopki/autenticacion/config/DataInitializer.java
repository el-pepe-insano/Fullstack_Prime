package com.Nopki.autenticacion.config;

import com.Nopki.autenticacion.model.Rol;
import com.Nopki.autenticacion.model.Usuario;
import com.Nopki.autenticacion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Cargando usuarios iniciales...");

            usuarioRepository.save(Usuario.builder()
                    .nombre("Admin Nopki")
                    .email("admin@nopki.com")
                    .contrasena(passwordEncoder.encode("password123"))
                    .rol(Rol.ADMIN)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .nombre("Juan Pérez")
                    .email("juan@gmail.com")
                    .contrasena(passwordEncoder.encode("password123"))
                    .rol(Rol.CLIENTE)
                    .build());

            log.info("✅ {} usuarios cargados", usuarioRepository.count());
        } else {
            log.info("Usuarios ya existentes, omitiendo carga inicial");
        }
    }
}