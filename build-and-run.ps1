# Script para compilar e executar o projeto Phantom
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Phantom - Build and Run" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Compilar
& .\compile.ps1

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    # Executar
    & .\run.ps1
} else {
    Write-Host "Não foi possível executar devido a erros de compilação." -ForegroundColor Red
    exit 1
}
