package com.iesalquerias.service.scraper;

import com.iesalquerias.model.Tiempo;
import com.iesalquerias.repository.TiempoRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatosAemetMurcia {

    @Autowired
    private TiempoRepository tiempoRepository;

    public void scrapeDatosMurcia() {
        String url = "https://www.aemet.es/es/eltiempo/observacion/ultimosdatos?k=mur&l=7178I&w=0&datos=det&x=h24&f=temperatura";

        try {
            Document document = Jsoup.connect(url).get();
            List<Tiempo> tiempos = generarTiempos(document, url);
            guardarTiempos(tiempos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Tiempo> generarTiempos(Document document, String url) {
        List<Tiempo> tiempos = new ArrayList<>();
        Elements rows = document.select("table#table tbody tr");

        for (Element row : rows) {
            Tiempo datosTiempo = generarTiempo(row, url);
            if (datosTiempo != null) {
                tiempos.add(datosTiempo);
            }
        }

        return tiempos;
    }

    private Tiempo generarTiempo(Element row, String url) {
        Elements columns = row.select("td");
        String fechaHora = columns.get(0).text();
        String temperatura = columns.get(1).text();
        String precipitacion = columns.get(6).text();

        if (!temperatura.isEmpty() && !precipitacion.isEmpty()) {
            return new Tiempo(
                    parseFecha(fechaHora),
                    Double.parseDouble(temperatura),
                    Double.parseDouble(precipitacion),
                    url,
                    true
            );
        }
        return null;
    }

    private void guardarTiempos(List<Tiempo> tiempos) {
        for (Tiempo tiempo : tiempos) {
            if (!existeTiempo(tiempo)) {
                tiempoRepository.save(tiempo);
                System.out.println("Datos Guardados");
            } else {
                System.out.println("Los datos ya existen en la base de datos");
            }
        }
    }

    private boolean existeTiempo(Tiempo tiempo) {
        return tiempoRepository.existsByFechaHoraAndDatosPrevision(tiempo.getFechaHora(), tiempo.isDatosPrevision());
    }

    private static LocalDateTime parseFecha(String fechaHora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.parse(fechaHora, formatter);
    }
}
