#!/usr/bin/env bash
set -euo pipefail
TYPE="${1:-app-image}"
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
mvn -B -DskipTests package
mkdir -p target/dist
ICON="src/main/resources/com/sofoste/arduino/icon.png"
if [[ "$(uname -s)" == "Darwin" ]]; then
  ICONSET="target/AppIcon.iconset"
  rm -rf "$ICONSET"
  mkdir -p "$ICONSET"
  for size in 16 32 128 256 512; do
    sips -z "$size" "$size" "$ICON" --out "$ICONSET/icon_${size}x${size}.png" >/dev/null
    double=$((size * 2))
    sips -z "$double" "$double" "$ICON" --out "$ICONSET/icon_${size}x${size}@2x.png" >/dev/null
  done
  iconutil -c icns "$ICONSET" -o target/AppIcon.icns
  ICON="target/AppIcon.icns"
fi
args=(
  --type "$TYPE"
  --input target/package-input
  --main-jar arduino-mission-control.jar
  --main-class com.sofoste.arduino.Launcher
  --name Arduino-Mission-Control
  --dest target/dist
  --app-version 2.0.0
  --vendor "Stephane Sob Fouodji"
  --description "Serial telemetry and diagnostics for Arduino-compatible boards"
  --copyright "Copyright 2026 Stephane Sob Fouodji"
  --java-options "-Dfile.encoding=UTF-8"
  --icon "$ICON"
)
if [[ "$TYPE" == "deb" ]]; then
  args+=(--linux-shortcut --linux-menu-group Development --linux-package-name arduino-mission-control)
elif [[ "$TYPE" == "dmg" ]]; then
  args+=(--mac-package-identifier io.github.sofoste93.arduino-mission-control)
fi
jpackage "${args[@]}"
