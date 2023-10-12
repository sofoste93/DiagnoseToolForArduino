# Dokumentation:

## DiagnoseTool Erweiterung für Arduino in Java (Verwendung von COM5)

> Diese erweiterte Version ermöglicht es, den Auswertevorgang des Arduino über die serielle Schnittstelle zu steuern. Sie können den Vorgang starten, anhalten und die zu überwachenden Pins konfigurieren.

### Steuerbefehle:
>
> START: Beginnt den Auswertevorgang und schreibt die Daten in "daten.txt".
> PAUSE: Hält den Auswertevorgang an.
> PINS:<pin1,pin2,...>: Konfiguriert die zu überwachenden Pins.
> STOP: Stoppt den Auswertevorgang.
> QUIT: Beendet das Programm.
> Wichtige Hinweise:
>
>
Die Kommunikation erfolgt über Baudrate 9600. Stellen Sie sicher, dass der Arduino-Code dieselbe Baudrate verwendet.
Das Programm verbindet sich direkt mit "COM5". Bei Bedarf können Sie den Port im Code ändern.
Die Befehle sollten in der angegebenen Reihenfolge und Format eingegeben werden, damit sie korrekt interpretiert werden können.
Mit diesen Änderungen können Sie den Arduino effektiv über die serielle Schnittstelle von Ihrem PC aus steuern.