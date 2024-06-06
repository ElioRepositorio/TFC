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
public class PrevisionAemetMurcia {

    private static final String URL_AEMET = "https://www.aemet.es/es/eltiempo/prediccion/municipios/horas/tabla/murcia-id30030";

    @Autowired
    private TiempoRepository tiempoRepository;

    public void scrapeDatosMurcia() {
        try {
            Document doc = Jsoup.connect(URL_AEMET).get();
            List<Tiempo> tiempos = parseDocument(doc);
            tiempos.forEach(tiempoRepository::save);
            System.out.println("Datos de AEMET Guardados");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Tiempo> parseDocument(Document document) {
        List<Tiempo> tiempos = new ArrayList<>();
        Elements rows = document.select("#tabla_prediccion tbody tr");

        String fecha = "";
        int rowspan = 0;
        boolean firstRowProcessed = false;

        for (Element row : rows) {
            Element fechaCell = row.select("th.fondo_azul_4F86D9[rowspan]").first();

            if (fechaCell != null) {
                fecha = fechaCell.text().trim();
                rowspan = Integer.parseInt(fechaCell.attr("rowspan"));
                firstRowProcessed = true;
            }

            if (!firstRowProcessed && (fecha.isEmpty() || rowspan == 0)) {
                continue;
            }

            if (!firstRowProcessed && fecha.isEmpty()) {
                fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } else {
                String[] fechaParts = fecha.split("\\s+");
                if (fechaParts.length > 1) {
                    String dia = fechaParts[1];
                    fecha = dia + "." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM.yyyy"));
                }
            }

            Tiempo tiempo = generadorTiempo(row, fecha);
            if (tiempo != null) {
                tiempos.add(tiempo);
            }

            rowspan--;
            if (rowspan == 0) {
                fecha = "";
            }
        }
        return tiempos;
    }

    private Tiempo generadorTiempo(Element row, String fecha) {
        String hora = row.select("td:eq(0)").text();
        String temperaturaStr = row.select("td:eq(2)").text();
        double temperatura = parseDouble(temperaturaStr);
        String precipitacion = row.select("td:eq(6)").text();

        if (!hora.isEmpty()) {
            if (!hora.contains(":")) {
                hora += ":00";
            }
            double precipitacionDouble = convertirPrecipitacion(precipitacion);
            LocalDateTime dateTime = LocalDateTime.parse(fecha + " " + hora, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            if (!tiempoRepository.existsByFechaHoraAndUrl(dateTime, URL_AEMET)) {
                Tiempo tiempo = new Tiempo(dateTime, temperatura, precipitacionDouble, URL_AEMET, false);
                tiempo.setFechaHoraExtraccion(LocalDateTime.now());
                return tiempo;
            }
        }
        return null;
    }

    private static double convertirPrecipitacion(String precipitacion) {
        if (precipitacion.trim().isEmpty()) {
            return 0.0;
        } else {
            precipitacion = precipitacion.replace(',', '.');
            if (precipitacion.indexOf('.') != precipitacion.lastIndexOf('.')) {
                precipitacion = precipitacion.substring(0, precipitacion.indexOf('.') + 1);
            }

            try {
                return Double.parseDouble(precipitacion);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    private static double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
