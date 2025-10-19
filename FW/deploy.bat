@echo off

:: Variables pour la librairie FrontServlet
set APP_NAME=FrameworkServlet
set SRC_DIR=src\main\java
set BUILD_DIR=build
set LIB_DIR=lib
set SERVLET_API_JAR=%LIB_DIR%\*
set TEST_LIB_DIR=..\testFramework_S5\lib

:: Nettoyage
if exist %BUILD_DIR% (
    rmdir /s /q %BUILD_DIR%
)
mkdir %BUILD_DIR%

:: Compilation
echo Compilation de la librairie FrontServlet...
dir /b /s %SRC_DIR%\*.java > sources.txt
javac -cp "%SERVLET_API_JAR%" -d %BUILD_DIR% @sources.txt
if errorlevel 1 (
    echo Erreur de compilation!
    del sources.txt
    pause
    exit /b 1
)
del sources.txt

:: Création du JAR
echo Creation du JAR %APP_NAME%.jar...
cd %BUILD_DIR%
jar -cvf %APP_NAME%.jar com
cd ..

:: Copie vers le projet Test
if not exist %TEST_LIB_DIR% (
    mkdir %TEST_LIB_DIR%
)
copy /Y %BUILD_DIR%\%APP_NAME%.jar %TEST_LIB_DIR%\

set REFLECTIONS_JAR=lib\reflections-0.10.2.jar
set JAVASSIST_JAR=lib\javassist-3.31.0-G2.jar

:: Compilation avec Reflections
javac -cp "%SERVLET_API_JAR%;%REFLECTIONS_JAR%;%JAVASSIST_JAR%" -d %BUILD_DIR% @sources.txt

:: Execution avec Reflections
java -cp "%BUILD_DIR%;%SERVLET_API_JAR%;%REFLECTIONS_JAR%;%JAVASSIST_JAR%" com.framework.servlet.AnnotationReader

echo.
echo Librairie FrontServlet buildée avec succes!
echo Copie vers: %TEST_LIB_DIR%\%APP_NAME%.jar
echo.
pause