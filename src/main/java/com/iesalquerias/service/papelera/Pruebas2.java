package com.iesalquerias.service.papelera;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Pruebas2 {
    public static void main(String[] args) {
        // Configurar ChromeDriverService para especificar un puerto
        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"))
                .usingPort(9515)
                .build();
        
        try {
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Inicializar ChromeOptions
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(service, options);

        // Abrir la página web
        driver.get("https://www.eltiempo.es/murcia.html?v=por_hora");

        try {
            // Esperar un momento para asegurar que la página se haya cargado completamente
            Thread.sleep(Duration.ofSeconds(5).toMillis()); // Convierte 5 segundos a milisegundos

            // Aceptar las cookies si se muestra el botón de aceptar
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement cookieAcceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Aceptar y continuar gratis')]")));
            if (cookieAcceptButton.isDisplayed()) {
                cookieAcceptButton.click();
                System.out.println("Botón de aceptar cookies clickeado.");
                // Esperar un momento adicional después de aceptar las cookies
                Thread.sleep(Duration.ofSeconds(3).toMillis()); // Puedes ajustar el tiempo según sea necesario
            }

            // Cerrar el anuncio si se muestra el botón de cerrar
            WebElement closeButton = driver.findElement(By.cssSelector("button.etgam-intersitial-modal-close[data-etgam-intersitial-modal-close='']"));
            if (closeButton.isDisplayed()) {
                closeButton.click();
                System.out.println("Botón de cerrar anuncio clickeado.");
            }

            // Esperar un momento adicional para cargar los datos
            Thread.sleep(Duration.ofSeconds(5).toMillis());

            // Encontrar todos los elementos de hora
            List<WebElement> horas = driver.findElements(By.cssSelector("#meteograma > li:nth-child(1) > ul > li > div:nth-child(1) > p.text-roboto-condensed.hours"));

            // Encontrar todos los elementos de temperatura
            List<WebElement> temperaturas = driver.findElements(By.cssSelector("#meteograma > li:nth-child(1) > ul > li > div:nth-child(1) > p.degrees"));

            // Encontrar todos los elementos de precipitación
            List<WebElement> precipitaciones = driver.findElements(By.cssSelector("#meteograma > li:nth-child(1) > ul > li > div:nth-child(2) > div.precipitations > p.measure"));

            // Verificar si las listas tienen el mismo tamaño
            if (horas.size() != temperaturas.size() || horas.size() != precipitaciones.size()) {
                System.out.println("El número de elementos de hora, temperatura y precipitación no coincide.");
                return;
            }

            // Iterar sobre los elementos y extraer los datos
            for (int i = 0; i < horas.size(); i++) {
                String hora = horas.get(i).getText();
                String temperatura = temperaturas.get(i).getText();
                String precipitacion = precipitaciones.get(i).getText();

                // Imprimir los datos
                System.out.println("Hora: " + hora);
                System.out.println("Temperatura: " + temperatura);
                System.out.println("Precipitación: " + precipitacion);
                System.out.println();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ocurrió un error: " + e.getMessage());
        } finally {
            // Cerrar el navegador al finalizar
            driver.quit();
            try {
                service.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

