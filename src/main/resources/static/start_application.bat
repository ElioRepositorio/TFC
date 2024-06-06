@echo off
:: Ruta al directorio de instalación de XAMPP  CAMBIAR SI XAMP ESTA EN OTRA RUTA
set XAMPP_PATH=C:\xampp

:: Navegar al directorio de XAMPP y abrir XAMPP Control Panel
cd %XAMPP_PATH%
start "" xampp-control.exe

:: Esperar unos segundos para que XAMPP Control Panel se abra
timeout /t 5

:: Iniciar Apache y MySQL
%XAMPP_PATH%\xampp_start.exe

:: Esperar unos segundos para asegurarse de que Apache y MySQL se inicien completamente
timeout /t 10

:: Navegar al directorio donde se encuentra tu JAR   ESTA ES LA LINEA QUE HAY QUE CAMBIAR POR DONDE HUBIQUES EL JAR
cd C:\Users\ManTequila\Documents\NetBeansProjects\MeteoScraper\target

:: Establecer JAVA_HOME si es necesario
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

:: Iniciar la aplicación Spring Boot usando el JAR 
start "" java -jar MeteoScraper-0.0.1-SNAPSHOT.jar  

:: Esperar unos segundos para asegurarse de que la aplicación se inicie completamente
timeout /t 30

:: Ejecutar la tarea programada llamando al endpoint de scraping
curl http://localhost:8080/ultimos_datos

echo Aplicación y tarea de scraping ejecutadas.

:: Esperar unos segundos para asegurar que el endpoint se llame correctamente
timeout /t 10

:: Detener la aplicación Java
for /f "tokens=2 delims=," %%A in ('tasklist /FI "IMAGENAME eq java.exe" /FO CSV /NH') do (
    taskkill /F /PID %%A
)

:: Detener Apache y MySQL de XAMPP
%XAMPP_PATH%\xampp_stop.exe

:: Cerrar XAMPP Control Panel
taskkill /IM xampp-control.exe /F

echo Todo ha sido cerrado.
pause


