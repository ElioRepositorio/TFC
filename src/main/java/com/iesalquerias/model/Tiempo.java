package com.iesalquerias.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "tiempo")

public class Tiempo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    private double temperatura;
    private double precipitaciones;
    private String url;

    @Column(name = "fecha_hora_extraccion")
    private LocalDateTime fechaHoraExtraccion;

    @Column(name = "datos_prevision")
    private boolean datosPrevision;

    public Tiempo() {
        // Constructor vacío requerido por JPA
    }

    public Tiempo(LocalDateTime fechaHora, double temperatura, double precipitaciones, String url,  boolean datosPrevision) {
        this.fechaHora = fechaHora;
        this.temperatura = temperatura;
        this.precipitaciones = precipitaciones;
        this.url = url;
        this.fechaHoraExtraccion = LocalDateTime.now();
        this.datosPrevision = datosPrevision;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getPrecipitaciones() {
        return precipitaciones;
    }

    public void setPrecipitaciones(double precipitaciones) {
        this.precipitaciones = precipitaciones;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getFechaHoraExtraccion() {
        return fechaHoraExtraccion;
    }

    public void setFechaHoraExtraccion(LocalDateTime fechaHoraExtraccion) {
        this.fechaHoraExtraccion = fechaHoraExtraccion;
    }

    public boolean isDatosPrevision() {
        return datosPrevision;
    }
    

    public void setDatosPrevision(boolean datosPrevision) {
        this.datosPrevision = datosPrevision;
    }

    @Override
    public String toString() {
        return "Tiempo{" +
                "id=" + id +
                ", fechaHora=" + fechaHora +
                ", temperatura=" + temperatura +
                ", precipitaciones=" + precipitaciones +
                ", url='" + url + '\'' +
                ", fechaHoraExtraccion=" + fechaHoraExtraccion +
                ", datosPrevision=" + datosPrevision +
                '}';
    }
}
