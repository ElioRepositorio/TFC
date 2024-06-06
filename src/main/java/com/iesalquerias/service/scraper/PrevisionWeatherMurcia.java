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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PrevisionWeatherMurcia {

    @Autowired
    private TiempoRepository tiempoRepository;

    public void scrapeDatosWeather() {
        String url = "https://weather.com/es-ES/tiempo/horario/l/447fdb4c3a431d2dbe0b775a7ab09b35ce39cee060e4cc5c9bb4d04e0dc88b36";
        List<Tiempo> tiempos = new ArrayList<>();

        try {
            // Conectarse a la página web y obtener el documento HTML
            Document document = Jsoup.connect(url).get();

            // Obtener el año actual
            int currentYear = LocalDate.now().getYear();

            // Obtener los elementos que contienen las fechas
            Elements fechaElements = document.select("h2.HourlyForecast--longDate--J_Pdh");

            DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder()
                    .appendPattern("[EEEE, d 'de' MMMM][EEEE, dd 'de' MMMM]")
                    .parseDefaulting(java.time.temporal.ChronoField.YEAR, currentYear)
                    .toFormatter(new Locale("es", "ES"));

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

            for (Element fechaElement : fechaElements) {
                String fechaString = fechaElement.text();
                LocalDate fechaLocalDate = LocalDate.parse(fechaString, inputFormatter);
                String fechaFormateada = fechaLocalDate.format(outputFormatter);

                // Obtener los detalles de la hora, temperatura, etc. correspondientes a esta fecha
                Element parent = fechaElement.parent();
                Elements detalles = parent.select(".DaypartDetails--DayPartDetail--2XOOV");

                // Iterar sobre los detalles y extraer la información necesaria
                for (Element detalle : detalles) {
                    Element horaElement = detalle.selectFirst(".DetailsSummary--daypartName--kbngc");
                    String hora = horaElement.text();

                    Element temperaturaElement = detalle.selectFirst(".DetailsSummary--tempValue--jEiXE");
                    String temperatura = temperaturaElement.text().replace("°", "");

                    Element precipElement = detalle.selectFirst("span[data-testid=AccumulationValue]");
                    String precip = precipElement != null ? precipElement.text().replace(" mm", "") : "0";

                    // Crear objeto Tiempo
                    LocalDateTime fechaHora = LocalDateTime.parse(fechaFormateada + " " + hora, DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm", Locale.ENGLISH));
                    double temp = Double.parseDouble(temperatura);
                    double precipValue = Double.parseDouble(precip);

                    if (!tiempoRepository.existsByFechaHoraAndUrl(fechaHora, url)) {
                        Tiempo tiempo = new Tiempo(fechaHora, temp, precipValue, url, false);
                        tiempo.setFechaHoraExtraccion(LocalDateTime.now());
                        tiempos.add(tiempo);
                    }
                }
            }

            // Guardar los objetos Tiempo en la base de datos
            tiempoRepository.saveAll(tiempos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
