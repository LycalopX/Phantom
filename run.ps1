# Script para executar o projeto Phantom
Write-Host "Iniciando Phantom..." -ForegroundColor Cyan

# Verificar se o projeto foi compilado
if (!(Test-Path "build\classes")) {
    Write-Host "Projeto não compilado. Execute .\compile.ps1 primeiro." -ForegroundColor Red
    exit 1
}

# Definir classpath
$libs = @(
    "lib\tinysound.jar",
    "lib\jlayer-1.0.1.jar",
    "lib\json-20250517.jar",
    "lib\mp3spi-1.9.5-1.jar",
    "lib\tritonus_share-0.3.6.jar"
)
$classpath = "build\classes;" + ($libs -join ";")

# Executar o jogo
java -cp $classpath Main
