package com.iesalquerias.repository;

import com.iesalquerias.model.Tiempo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TiempoRepository extends JpaRepository<Tiempo, Integer> {

    boolean existsByFechaHoraAndDatosPrevision(LocalDateTime fechaHora, boolean datosPrevision);

    List<Tiempo> findByDatosPrevision(boolean b);

    boolean existsByFechaHoraAndUrl(LocalDateTime fechaHora, String url);

    @Query("SELECT t FROM Tiempo t "
            + "WHERE t.fechaHoraExtraccion BETWEEN :fechaHoraMinusTenSeconds AND :fechaHoraPlusTenSeconds")
    List<Tiempo> findLastRecords(@Param("fechaHoraMinusTenSeconds") LocalDateTime fechaHoraMinusTenSeconds,
            @Param("fechaHoraPlusTenSeconds") LocalDateTime fechaHoraPlusTenSeconds);

    @Query("SELECT t FROM Tiempo t "
            + "WHERE t.fechaHora = :fechaHora "
            + "AND t.datosPrevision = true "
            + "AND EXISTS ("
            + "    SELECT 1 FROM Tiempo t2 "
            + "    WHERE t2.fechaHora = :fechaHora "
            + "    AND t2.datosPrevision = false "
            + "    AND t.temperatura IS NOT NULL "
            + "    AND t.precipitaciones IS NOT NULL "
            + "    AND t.url IS NOT NULL "
            + ")")
    List<Tiempo> findByFechaHoraAndBothTypes(@Param("fechaHora") LocalDateTime fechaHora);

    @Query("SELECT t FROM Tiempo t WHERE DATE_FORMAT(t.fechaHora, '%Y-%m-%d') = DATE_FORMAT(:fechaHora, '%Y-%m-%d') AND t.url = :url")
    List<Tiempo> findByFechaHoraFormatoAndUrl(@Param("fechaHora") LocalDateTime fechaHora, @Param("url") String url);

    @Query("SELECT t FROM Tiempo t ORDER BY t.fechaHora DESC")
    List<Tiempo> findAllOrderedByFechaDesc();

    @Query("SELECT t.fechaHora AS fechaHora, "
            + "t.temperatura AS temperaturaAEMET, "
            + "t1.temperatura AS temperaturaMeteosat, "
            + "t2.temperatura AS temperaturaAEMETPrevision "
            + "FROM Tiempo t "
            + "JOIN Tiempo t1 ON t.fechaHora = t1.fechaHora AND t1.url = 'https://www.meteosat.com/tiempo/murcia/' "
            + "JOIN Tiempo t2 ON t.fechaHora = t2.fechaHora AND t2.url = 'https://www.aemet.es/es/eltiempo/prediccion/municipios/horas/tabla/murcia-id30030' "
            + "WHERE t.url = 'https://www.aemet.es/es/eltiempo/observacion/ultimosdatos?k=mur&l=7178I&w=0&datos=det&x=h24&f=temperatura' "
            + "AND t.temperatura IS NOT NULL")
    List<Object[]> findTemperaturaComparativa();

    @Query("SELECT t.fechaHora AS fechaHora, "
            + "t.precipitaciones AS precipitacionesAEMET, "
            + "t1.precipitaciones AS precipitacionesMeteosat, "
            + "t2.precipitaciones AS precipitacionesAEMETPrevision "
            + "FROM Tiempo t "
            + "JOIN Tiempo t1 ON t.fechaHora = t1.fechaHora AND t1.url = 'https://www.meteosat.com/tiempo/murcia/' "
            + "JOIN Tiempo t2 ON t.fechaHora = t2.fechaHora AND t2.url = 'https://www.aemet.es/es/eltiempo/prediccion/municipios/horas/tabla/murcia-id30030' "
            + "WHERE t.url = 'https://www.aemet.es/es/eltiempo/observacion/ultimosdatos?k=mur&l=7178I&w=0&datos=det&x=h24&f=temperatura' "
            + "AND t.precipitaciones IS NOT NULL "
            + "AND t1.precipitaciones IS NOT NULL "
            + "AND t2.precipitaciones IS NOT NULL")
    List<Object[]> findPrecipitacionesComparativa();

    List<Tiempo> findByFechaHora(LocalDateTime fechaHora);

    List<Tiempo> findByUrl(String url);

    List<Tiempo> findByFechaHoraAndUrl(LocalDateTime fechaHora, String url);

    @Query("SELECT DISTINCT t.url FROM Tiempo t")
    List<String> findDistinctUrls();

    @Query("SELECT t FROM Tiempo t WHERE DATE(t.fechaHora) = :fecha")
    List<Tiempo> findByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT t FROM Tiempo t WHERE DATE(t.fechaHora) = :fecha AND t.url = :url")
    List<Tiempo> findByFechaAndUrl(@Param("fecha") LocalDate fecha, @Param("url") String url);
     
}
