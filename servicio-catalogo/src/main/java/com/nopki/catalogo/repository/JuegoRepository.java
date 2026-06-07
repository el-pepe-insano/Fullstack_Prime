package com.nopki.catalogo.repository;

import com.nopki.catalogo.model.Genero;
import com.nopki.catalogo.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

    List<Juego> findByDisponible(boolean disponible);
    List<Juego> findByGenero(Genero genero);
    List<Juego> findByPlataforma(String plataforma);
    List<Juego> findByDesarrollador(String desarrollador);
    @Query("SELECT j FROM Juego j WHERE LOWER(j.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))")
    List<Juego> buscarPorTitulo(@Param("titulo") String titulo);

    @Query("SELECT j FROM Juego j WHERE j.precio BETWEEN :min AND :max AND j.disponible = true")
    List<Juego> buscarPorRangoPrecio(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    @Query("SELECT j FROM Juego j WHERE j.genero = :genero AND j.disponible = true")
    List<Juego> buscarDisponiblesPorGenero(@Param("genero") Genero genero);

    boolean existsById(Long id);
}