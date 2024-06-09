package com.iesalquerias.service.papelera;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Pruebas {
    public static void main(String[] args) {
        // Configurar el EdgeDriver usando WebDriverManager
        WebDriverManager.edgedriver().setup();

        // Inicializar EdgeOptions
        EdgeOptions options = new EdgeOptions();
        WebDriver driver = new EdgeDriver(options);

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
            try {
                WebElement closeButton = driver.findElement(By.cssSelector("button.etgam-intersitial-modal-close[data-etgam-intersitial-modal-close='']"));
                if (closeButton.isDisplayed()) {
                    closeButton.click();
                    System.out.println("Botón de cerrar anuncio clickeado.");
                }
            } catch (org.openqa.selenium.NoSuchElementException e) {
                // El botón de cerrar no está presente, sigue con el flujo normal
                System.out.println("El botón de cerrar anuncio no está presente.");
            }

            // Esperar un momento adicional para cargar los datos
            Thread.sleep(Duration.ofSeconds(5).toMillis());

            // Asegurarse de que todos los datos están cargados antes de extraerlos
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.days-title.text-poppins-bold")));

            // Encontrar todos los elementos de día
            List<WebElement> dias = driver.findElements(By.cssSelector("h2.days-title.text-poppins-bold"));

            for (WebElement dia : dias) {
                String diaTexto = dia.getText();
                System.out.println("Día: " + diaTexto);

                // Encontrar el contenedor del día
                WebElement contenedorDia = dia.findElement(By.xpath("./following-sibling::ul"));

                // Encontrar todos los elementos de hora en el contenedor del día
                List<WebElement> horas = contenedorDia.findElements(By.cssSelector("li > div:nth-child(1) > p.text-roboto-condensed.hours"));

                // Encontrar todos los elementos de temperatura en el contenedor del día
                List<WebElement> temperaturas = contenedorDia.findElements(By.cssSelector("li > div:nth-child(1) > p.degrees"));

                // Encontrar todos los elementos de precipitación en el contenedor del día
                List<WebElement> precipitaciones = contenedorDia.findElements(By.cssSelector("li > div:nth-child(2) > div.precipitations > p.measure"));

                // Verificar si las listas tienen el mismo tamaño
                if (horas.size() != temperaturas.size() || horas.size() != precipitaciones.size()) {
                    System.out.println("El número de elementos de hora, temperatura y precipitación no coincide.");
                    continue;
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
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ocurrió un error: " + e.getMessage());
        } finally {
            // Cerrar el navegador al finalizar
            driver.quit();
        }
    }
}
