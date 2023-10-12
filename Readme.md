# Diagnose-Tool für den Arduino

## TEIL 1
Dieses Programm verwendet die jSerialComm-Bibliothek, um eine serielle Verbindung zum Arduino über den Port "COM5" herzustellen.

Sobald eine Verbindung hergestellt ist, liest das Programm Daten von der seriellen Schnittstelle und schreibt diese Daten in eine Datei namens "daten.txt" oder "diagnoseLog.txt".

### Schritte zur Ausführung:

- 1. Fügen Sie die jSerialComm-Bibliothek zu Ihrem Projekt hinzu.
- 2. Führen Sie den DiagnoseTool-Code aus.
- 3. Überprüfen Sie die "daten.txt" ode "diagnoseLog.txt"-Datei auf die von Arduino gesendeten Daten.

### Wichtige Anmerkung:
- Stellen Sie sicher, dass der Baudrate-Wert (in diesem Fall 9600) dem Baudrate-Wert entspricht, der in Ihrem Arduino-Code festgelegt ist.
- Das Programm verbindet sich direkt mit "COM5". Falls Ihr Arduino an einem anderen Port angeschlossen ist, ändern Sie den Port entsprechend im Code.


## TEIL 2

### DiagnoseTool Erweiterung für Arduino in Java (Verwendung von COM5)

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
Die Kommunikation erfolgt über Baudrate 9600. 
Stellen Sie sicher, dass der Arduino-Code dieselbe Baudrate verwendet.
Das Programm verbindet sich direkt mit "COM5". Bei Bedarf können Sie den Port im Code ändern.
Die Befehle sollten in der angegebenen Reihenfolge und Format eingegeben werden, damit sie korrekt interpretiert werden können.
Mit diesen Änderungen können Sie den Arduino effektiv über die serielle Schnittstelle von Ihrem PC aus steuern.
>
> ### Contributions
> 
> @sofoste93/ Stephane Sob Fouodji
> Herr Franz /TLS
> Arduino