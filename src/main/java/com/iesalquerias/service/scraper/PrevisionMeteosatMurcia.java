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
public class PrevisionMeteosatMurcia {

    private static final String URL_METEOSAT = "https://www.meteosat.com/tiempo/murcia/";

    @Autowired
    private TiempoRepository tiempoRepository;

    public void scrapeDatosMurcia() {
        try {
            Document doc = Jsoup.connect(URL_METEOSAT).get();
            List<Tiempo> tiempos = parseDocument(doc);
            tiempos.forEach(tiempoRepository::save);
            System.out.println("Datos Meteosat Guardados");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Tiempo> parseDocument(Document document) {
        List<Tiempo> tiempos = new ArrayList<>();
        Element tablaPrevision = document.selectFirst("div.prevision-local table");
        Elements filas = tablaPrevision.select("tr");

        String fechaActual = "";

        for (Element fila : filas) {
            Element fechaElement = fila.selectFirst("th.col1 font");
            String fecha = (fechaElement != null) ? fechaElement.text() : fechaActual;

            if (!fecha.isEmpty() && !fecha.equals(fechaActual)) {
                fechaActual = fecha;
            }

            Element horaElement = fila.selectFirst("th.col2");
            String hora = (horaElement != null) ? horaElement.text() : "";

            if (hora.contains("de")) {
                continue;
            }

            if (!hora.isEmpty() && !hora.contains(":")) {
                hora = hora.replace(" h", ":00");
            }

            Element temperaturaElement = fila.selectFirst("td.pluss.col4");
            String temperaturaStr = (temperaturaElement != null) ? temperaturaElement.text().replaceAll("[^0-9]", "") : "";

            Element precipitacionesElement = fila.selectFirst("td.col7");
            String precipitacionesStr = (precipitacionesElement != null) ? precipitacionesElement.text().split(" ")[0] : "";

            if (!hora.isEmpty() && !temperaturaStr.isEmpty() && !precipitacionesStr.isEmpty()) {
                Tiempo tiempo = generadorTiempo(fecha, hora, temperaturaStr, precipitacionesStr);
                if (tiempo != null) {
                    tiempos.add(tiempo);
                }
            }
        }
        return tiempos;
    }

    private Tiempo generadorTiempo(String fecha, String hora, String temperaturaStr, String precipitacionesStr) {
        String[] partesFecha = fecha.split(" ");
        if (partesFecha.length > 1) {
            fecha = partesFecha[1];
        }
        String fechaHoraStr = fecha + " " + hora;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr, formatter);
        double temperatura = Double.parseDouble(temperaturaStr);
        double precipitaciones = Double.parseDouble(precipitacionesStr.replace(",", "."));

        if (!tiempoRepository.existsByFechaHoraAndUrl(fechaHora, URL_METEOSAT)) {
            Tiempo tiempo = new Tiempo(fechaHora, temperatura, precipitaciones, URL_METEOSAT, false);
            tiempo.setFechaHoraExtraccion(LocalDateTime.now());
            System.out.println("Dato Guardado para " + fechaHora);
            return tiempo;
        } else {
            System.out.println("Registro duplicado para " + fechaHora + ". Saltando...");
            return null;
        }
    }
}
