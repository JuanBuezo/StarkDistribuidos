$profile = '-Dspring-boot.run.profiles=local'

Write-Host "Iniciando StarkDistribuidos..."

Start-Job -Name "config" -ScriptBlock {
    Set-Location $using:PWD
    .\mvnw -pl starkDistribuidos-config spring-boot:run $using:profile
}

Start-Sleep -Seconds 10

$services = @(
    "starkDistribuidos-gateway",
    "starkDistribuidos-auth",
    "starkDistribuidos-sensor",
    "starkDistribuidos-alert",
    "starkDistribuidos-access",
    "starkDistribuidos-notification",
    "starkDistribuidos-frontend"
)

foreach ($service in $services) {
    Start-Job -Name $service -ScriptBlock {
        Set-Location $using:PWD
        .\mvnw -pl $using:service spring-boot:run $using:profile
    }
}

Write-Host "Servicios lanzados. Mostrando logs..."

while ($true) {
    Get-Job | ForEach-Object {
        Receive-Job -Job $_ -Keep
    }
    Start-Sleep -Seconds 2
}