@echo off
setlocal enabledelayedexpansion

REM Project paths
set "ROOT_DIR=%~dp0"
set "ROOT_DIR=%ROOT_DIR:~0,-1%"
set "SRC_DIR=%ROOT_DIR%\src\main\java"
set "LIB_DIR=%ROOT_DIR%\lib"
set "TARGET_DIR=%ROOT_DIR%\target"
set "CLASSES_DIR=%TARGET_DIR%\classes"
set "JAR_FILE=%TARGET_DIR%\MyFramework.jar"
set "MANIFEST_FILE=%TARGET_DIR%\MANIFEST.MF"
set "DEST_TOMCAT_LIB=E:\apache-tomcat-11.0.11\apache-tomcat-11.0.11\lib"
set "DEST_JAR=%DEST_TOMCAT_LIB%\MyFramework.jar"

REM Ensure tools
where javac >nul 2>&1
if errorlevel 1 (
    echo Error: javac not found. Install JDK.
    pause
    exit /b 1
)

where jar >nul 2>&1
if errorlevel 1 (
    echo Error: jar tool not found. Install JDK.
    pause
    exit /b 1
)

REM Prepare directories
if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"

REM Find servlet API JAR
set "CP="
if exist "%LIB_DIR%\servlet-api.jar" (
    set "CP=%LIB_DIR%\servlet-api.jar"
) else if exist "%LIB_DIR%\jakarta.servlet-api.jar" (
    set "CP=%LIB_DIR%\jakarta.servlet-api.jar"
) else (
    REM Try to find jakarta.servlet-api in Tomcat lib
    for %%f in ("%DEST_TOMCAT_LIB%\servlet-api-*.jar") do (
        if exist "%%f" (
            set "CP=%%f"
            goto :found_cp
        )
    )
    for %%f in ("%DEST_TOMCAT_LIB%\jakarta.servlet-api-*.jar") do (
        if exist "%%f" (
            set "CP=%%f"
            goto :found_cp
        )
    )
)

:found_cp
if "%CP%"=="" (
    echo Error: Could not locate jakarta.servlet-api jar for compilation.
    echo Hints:
    echo   - Copy your Tomcat's jakarta.servlet-api-^<ver^>.jar into %LIB_DIR% as jakarta.servlet-api.jar
    echo   - Or set DEST_TOMCAT_LIB correctly and ensure it contains jakarta.servlet-api-*.jar
    pause
    exit /b 1
)

REM Collect sources
set "SOURCES_LIST=%TARGET_DIR%\sources.txt"
dir /s /b "%SRC_DIR%\*.java" > "%SOURCES_LIST%" 2>nul

if not exist "%SOURCES_LIST%" (
    echo Error: No Java sources found under %SRC_DIR%
    pause
    exit /b 1
)

for /f %%i in ("%SOURCES_LIST%") do (
    if %%~zi equ 0 (
        echo Error: No Java sources found under %SRC_DIR%
        pause
        exit /b 1
    )
)

REM Build classpath with all JARs in lib
set "FULL_CP=%CP%"
for %%f in ("%LIB_DIR%\*.jar") do (
    if not "%%f"=="%CP%" (
        set "FULL_CP=!FULL_CP!;%%f"
    )
)

REM Compile
echo Compiling sources...
javac -cp "%FULL_CP%" -d "%CLASSES_DIR%" @"%SOURCES_LIST%"
if errorlevel 1 (
    echo Compilation failed
    pause
    exit /b 1
)

REM Create Manifest
(
echo Manifest-Version: 1.0
echo Created-By: build.bat
echo.
) > "%MANIFEST_FILE%"

REM Package JAR
echo Packaging %JAR_FILE% ...
jar cfm "%JAR_FILE%" "%MANIFEST_FILE%" -C "%CLASSES_DIR%" .
if errorlevel 1 (
    echo JAR creation failed
    pause
    exit /b 1
)

REM Deploy to Tomcat lib
echo Copying framework JAR to %DEST_JAR% ...
if exist "%DEST_JAR%" (
    attrib -r "%DEST_JAR%" 2>nul
)

copy /y "%JAR_FILE%" "%DEST_JAR%" >nul 2>&1
if errorlevel 1 (
    echo Direct copy failed ^(may need admin rights^). Trying with admin...
    
    REM Try with PowerShell for elevation
    powershell -Command "Start-Process -Verb RunAs -FilePath 'cmd.exe' -ArgumentList '/c copy /y \"%JAR_FILE%\" \"%DEST_JAR%\"'"
    
    REM Wait a moment for copy to complete
    timeout /t 2 /nobreak >nul
    
    if exist "%DEST_JAR%" (
        echo Copied to %DEST_JAR% with admin rights
    ) else (
        echo Warning: Failed to copy to %DEST_JAR%.
        echo Please copy %JAR_FILE% to %DEST_JAR% manually with admin permissions.
    )
) else (
    echo Copied to %DEST_JAR%
)

REM Also deploy framework dependencies
echo Deploying dependencies...
for %%f in ("%LIB_DIR%\commons-beanutils-*.jar" "%LIB_DIR%\commons-logging-*.jar") do (
    if exist "%%f" (
        set "dest_dep=%DEST_TOMCAT_LIB%\%%~nxf"
        echo Copying dependency %%~nxf to !dest_dep! ...
        
        copy /y "%%f" "!dest_dep!" >nul 2>&1
        if errorlevel 1 (
            echo Failed to copy %%~nxf. May need admin rights.
        ) else (
            echo Copied %%~nxf
        )
    )
)

REM Done
echo.
echo Built JAR: %JAR_FILE%
echo.
pause