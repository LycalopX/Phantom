# Script para compilar o projeto Phantom
Write-Host "Compilando o projeto..." -ForegroundColor Cyan

# Criar diretório de build se não existir
if (!(Test-Path "build\classes")) {
    New-Item -ItemType Directory -Force -Path "build\classes" | Out-Null
}

# Definir classpath com todas as bibliotecas
$libs = @(
    "lib\tinysound.jar",
    "lib\jlayer-1.0.1.jar",
    "lib\json-20250517.jar",
    "lib\mp3spi-1.9.5-1.jar",
    "lib\tritonus_share-0.3.6.jar"
)
$classpath = ($libs -join ";")

# Compilar todos os arquivos .java
$sourceFiles = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }

Write-Host "Compilando $($sourceFiles.Count) arquivos..." -ForegroundColor Yellow

javac -encoding UTF-8 -d "build\classes" -cp $classpath ($sourceFiles -join " ")

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilação concluída com sucesso!" -ForegroundColor Green
    
    # Copiar recursos para o diretório de build
    Write-Host "Copiando recursos..." -ForegroundColor Yellow
    Copy-Item -Path "src\imgs" -Destination "build\classes\imgs" -Recurse -Force
    Copy-Item -Path "src\recursos" -Destination "build\classes\recursos" -Recurse -Force
    Copy-Item -Path "src\sounds" -Destination "build\classes\sounds" -Recurse -Force
    
    Write-Host "Recursos copiados!" -ForegroundColor Green
} else {
    Write-Host "Erro na compilação!" -ForegroundColor Red
    exit 1
}
