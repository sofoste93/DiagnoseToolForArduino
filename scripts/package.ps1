param([ValidateSet("app-image", "exe", "msi")][string]$PackageType = "app-image")
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $root
mvn -B -DskipTests package
if ($LASTEXITCODE -ne 0) { throw "Maven package failed." }
$arguments = @(
  "--type", $PackageType,
  "--input", "target\package-input",
  "--main-jar", "arduino-mission-control.jar",
  "--main-class", "com.sofoste.arduino.Launcher",
  "--name", "Arduino-Mission-Control",
  "--dest", "target\dist",
  "--app-version", "2.0.0",
  "--vendor", "Stephane Sob Fouodji",
  "--description", "Serial telemetry and diagnostics for Arduino-compatible boards",
  "--copyright", "Copyright 2026 Stephane Sob Fouodji",
  "--java-options", "-Dfile.encoding=UTF-8",
  "--icon", "src\main\resources\com\sofoste\arduino\app.ico"
)
if ($PackageType -in @("exe", "msi")) {
  $arguments += @("--win-menu", "--win-shortcut", "--win-dir-chooser", "--win-menu-group", "Arduino Mission Control")
}
New-Item -ItemType Directory -Path "target\dist" -Force | Out-Null
jpackage @arguments
if ($LASTEXITCODE -ne 0) { throw "jpackage failed." }
if ($PackageType -eq "exe" -and $env:WINDOWS_CERTIFICATE_BASE64 -and $env:WINDOWS_CERTIFICATE_PASSWORD) {
  $certificate = Join-Path $env:RUNNER_TEMP "arduino-mission-control.pfx"
  [IO.File]::WriteAllBytes($certificate, [Convert]::FromBase64String($env:WINDOWS_CERTIFICATE_BASE64))
  $installer = Get-ChildItem "target\dist\*.exe" | Select-Object -First 1
  & signtool sign /fd SHA256 /tr "http://timestamp.digicert.com" /td SHA256 /f $certificate /p $env:WINDOWS_CERTIFICATE_PASSWORD $installer.FullName
  if ($LASTEXITCODE -ne 0) { throw "Authenticode signing failed." }
  Remove-Item -LiteralPath $certificate -Force
}
