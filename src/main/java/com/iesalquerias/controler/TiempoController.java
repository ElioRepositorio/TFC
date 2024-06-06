package com.iesalquerias.controler;

import com.iesalquerias.model.Tiempo;
import com.iesalquerias.repository.TiempoRepository;
import com.iesalquerias.service.scraper.DatosAemetMurcia;
import com.iesalquerias.service.scraper.PrevisionAemetMurcia;
import com.iesalquerias.service.scraper.PrevisionMeteosatMurcia;
import com.iesalquerias.service.scraper.PrevisionWeatherMurcia;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TiempoController {

    @Autowired
    private TiempoRepository tiempoRepository;

    @Autowired
    private PrevisionAemetMurcia previsionAemetMurcia;

    @Autowired
    private DatosAemetMurcia datosAemetMurcia;

    @Autowired
    private PrevisionMeteosatMurcia previsionMeteosatMurcia;

    @Autowired
    private PrevisionMeteosatMurcia datosMeteosatMurcia;

    @Autowired
    private PrevisionWeatherMurcia previsionWeatherMurcia;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/ultimos_datos")
    public String scrape(Model model, RedirectAttributes redirectAttributes) {
        // Scrapear los datos y guardarlos
        previsionAemetMurcia.scrapeDatosMurcia();
        datosAemetMurcia.scrapeDatosMurcia();
        previsionMeteosatMurcia.scrapeDatosMurcia();
        datosMeteosatMurcia.scrapeDatosMurcia();
        previsionWeatherMurcia.scrapeDatosWeather();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fechaHoraMinusTenSeconds = now.minusSeconds(10);
        LocalDateTime fechaHoraPlusTenSeconds = now.plusSeconds(10);

        List<Tiempo> ultimosDatos = tiempoRepository.findLastRecords(fechaHoraMinusTenSeconds, fechaHoraPlusTenSeconds);

        model.addAttribute("ultimosDatos", ultimosDatos);

        redirectAttributes.addFlashAttribute("successMessage", "Datos raspados y guardados exitosamente.");

        return "ultimos_datos";
    }

    @GetMapping("/programador_tareas")
    public String getScheduledTask(Model model) {

        return "programador_tareas";
    }

    @GetMapping("/all_data")
    public String mostrarTodosDatos(Model model) {

        List<Tiempo> todosLosDatos = tiempoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaHora"));

        model.addAttribute("todosLosDatos", todosLosDatos);

        return "all_data";
    }

    @GetMapping("/resultadosBusqueda")
    public String mostrarFormularioBusqueda(Model model) {
        // Obtener URLs únicas de la base de datos
        List<String> urls = tiempoRepository.findDistinctUrls();
        model.addAttribute("urls", urls);
        return "resultadosBusqueda";
    }

    @PostMapping("/resultadosBusqueda")
    public String buscarDatos(@RequestParam(value = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                              @RequestParam(value = "url", required = false) String url,
                              Model model) {
        List<Tiempo> resultados = null;

        if (fecha != null && url != null) {
            resultados = tiempoRepository.findByFechaAndUrl(fecha, url);
        } else if (fecha != null) {
            resultados = tiempoRepository.findByFecha(fecha);
        } else if (url != null) {
            resultados = tiempoRepository.findByUrl(url);
        }

        model.addAttribute("resultados", resultados);
        List<String> urls = tiempoRepository.findDistinctUrls();
        model.addAttribute("urls", urls);
        return "resultadosBusqueda";
    }

    @GetMapping("/graficos")
    public String mostrarGraficos(Model model) {
        List<Object[]> temperaturaComparativa = tiempoRepository.findTemperaturaComparativa();
        List<Object[]> precipitacionesComparativa = tiempoRepository.findPrecipitacionesComparativa();

        model.addAttribute("temperaturaComparativa", temperaturaComparativa);
        model.addAttribute("precipitacionesComparativa", precipitacionesComparativa);

        return "graficos";
    }

    @GetMapping("/eficiencia")
    public String mostrarEficiencia(Model model) {
        List<Object[]> temperaturaEficiencia = tiempoRepository.findTemperaturaComparativa();
        List<Object[]> precipitacionesEficiencia = tiempoRepository.findPrecipitacionesComparativa();

        model.addAttribute("temperaturaEficiencia", temperaturaEficiencia);
        model.addAttribute("precipitacionesEficiencia", precipitacionesEficiencia);
        return "eficiencia";
    }
}
