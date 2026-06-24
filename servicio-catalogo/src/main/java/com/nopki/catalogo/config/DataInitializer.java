package com.nopki.catalogo.config;

import com.nopki.catalogo.model.Genero;
import com.nopki.catalogo.model.Juego;
import com.nopki.catalogo.repository.JuegoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final JuegoRepository juegoRepository;

    @Override
    public void run(String... args) {
        if (juegoRepository.count() == 0) {
            log.info("Cargando juegos iniciales...");

            // Saga Resident Evil
            juegoRepository.save(Juego.builder()
                    .titulo("Resident Evil")
                    .descripcion("El juego que definió el survival horror: explora la mansión Spencer y sobrevive al horror")
                    .precio(new BigDecimal("9.99"))
                    .desarrollador("Capcom")
                    .plataforma("PC")
                    .genero(Genero.TERROR)
                    .imagenUrl("https://img.nopki.com/re1.jpg")
                    .fechaLanzamiento("1996-03-22")
                    .build());

            juegoRepository.save(Juego.builder()
                    .titulo("Resident Evil 2")
                    .descripcion("Leon y Claire sobreviven la noche más larga en Raccoon City")
                    .precio(new BigDecimal("14.99"))
                    .desarrollador("Capcom")
                    .plataforma("PC")
                    .genero(Genero.TERROR)
                    .imagenUrl("https://img.nopki.com/re2.jpg")
                    .fechaLanzamiento("1998-01-21")
                    .build());

            juegoRepository.save(Juego.builder()
                    .titulo("Resident Evil 3: Nemesis")
                    .descripcion("Jill Valentine huye de Nemesis mientras Raccoon City cae en el caos")
                    .precio(new BigDecimal("14.99"))
                    .desarrollador("Capcom")
                    .plataforma("PC")
                    .genero(Genero.TERROR)
                    .imagenUrl("https://img.nopki.com/re3.jpg")
                    .fechaLanzamiento("1999-09-22")
                    .build());

            juegoRepository.save(Juego.builder()
                    .titulo("Resident Evil 4")
                    .descripcion("Leon S. Kennedy viaja a Europa para rescatar a la hija del presidente")
                    .precio(new BigDecimal("19.99"))
                    .desarrollador("Capcom")
                    .plataforma("PC")
                    .genero(Genero.ACCION)
                    .imagenUrl("https://img.nopki.com/re4.jpg")
                    .fechaLanzamiento("2005-01-11")
                    .build());

            juegoRepository.save(Juego.builder()
                    .titulo("Resident Evil 7: Biohazard")
                    .descripcion("Regreso a las raíces del terror en primera persona en una mansión de Louisiana")
                    .precio(new BigDecimal("29.99"))
                    .desarrollador("Capcom")
                    .plataforma("PC")
                    .genero(Genero.TERROR)
                    .imagenUrl("https://img.nopki.com/re7.jpg")
                    .fechaLanzamiento("2017-01-24")
                    .build());

            juegoRepository.save(Juego.builder()
                    .titulo("Resident Evil Village")
                    .descripcion("Ethan Winters llega a un misterioso pueblo europeo para rescatar a su hija")
                    .precio(new BigDecimal("39.99"))
                    .desarrollador("Capcom")
                    .plataforma("PC")
                    .genero(Genero.TERROR)
                    .imagenUrl("https://img.nopki.com/revillage.jpg")
                    .fechaLanzamiento("2021-05-07")
                    .build());

            // Otros juegos
            juegoRepository.save(Juego.builder()
                    .titulo("The Witcher 3: Wild Hunt")
                    .descripcion("RPG de mundo abierto ambientado en un universo de fantasía oscura")
                    .precio(new BigDecimal("29.99"))
                    .desarrollador("CD Projekt Red")
                    .plataforma("PC")
                    .genero(Genero.RPG)
                    .imagenUrl("https://img.nopki.com/witcher3.jpg")
                    .fechaLanzamiento("2015-05-19")
                    .build());

            juegoRepository.save(Juego.builder()
                    .titulo("Hollow Knight")
                    .descripcion("Juego de acción y aventura en un reino subterráneo de insectos")
                    .precio(new BigDecimal("14.99"))
                    .desarrollador("Team Cherry")
                    .plataforma("PC")
                    .genero(Genero.AVENTURA)
                    .imagenUrl("https://img.nopki.com/hollowknight.jpg")
                    .fechaLanzamiento("2017-02-24")
                    .build());

            juegoRepository.save(Juego.builder()
                    .titulo("Hades")
                    .descripcion("Roguelike de acción donde escapas del inframundo griego")
                    .precio(new BigDecimal("24.99"))
                    .desarrollador("Supergiant Games")
                    .plataforma("PC")
                    .genero(Genero.ACCION)
                    .imagenUrl("https://img.nopki.com/hades.jpg")
                    .fechaLanzamiento("2020-09-17")
                    .build());

            juegoRepository.save(Juego.builder()
                    .titulo("Stardew Valley")
                    .descripcion("Simulador de granja donde construyes tu vida en el campo")
                    .precio(new BigDecimal("14.99"))
                    .desarrollador("ConcernedApe")
                    .plataforma("PC")
                    .genero(Genero.SIMULACION)
                    .imagenUrl("https://img.nopki.com/stardew.jpg")
                    .fechaLanzamiento("2016-02-26")
                    .build());

            log.info("✅ {} juegos cargados", juegoRepository.count());
        } else {
            log.info("Juegos ya existentes, omitiendo carga inicial");
        }
    }
}