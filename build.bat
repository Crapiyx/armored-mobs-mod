@echo off
echo Compilation du mod Armored Mobs...
echo.

where gradle >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR: Gradle n'est pas installe ou pas dans le PATH.
    echo.
    echo Telecharge Gradle depuis: https://gradle.org/releases/
    echo Extrais dans C:\Gradle\ et ajoute C:\Gradle\gradle-X.X\bin au PATH
    echo.
    pause
    exit /b 1
)

gradle build
if %errorlevel% equ 0 (
    echo.
    echo Succes! Le .jar est dans build\libs\
    dir build\libs\*.jar
) else (
    echo.
    echo Echec de la compilation.
)
pause
