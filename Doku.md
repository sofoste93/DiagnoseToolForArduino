DiagnoseTool für Arduino in Java (Verwendung von COM5)

Dieses Programm verwendet die jSerialComm-Bibliothek, um eine serielle Verbindung zum Arduino über den Port "COM5" herzustellen.

Sobald eine Verbindung hergestellt ist, liest das Programm Daten von der seriellen Schnittstelle und schreibt diese Daten in eine Datei namens "daten.txt".

Schritte zur Ausführung:

1. Fügen Sie die jSerialComm-Bibliothek zu Ihrem Projekt hinzu.
2. Führen Sie den DiagnoseTool-Code aus.
3. Überprüfen Sie die "daten.txt"-Datei auf die von Arduino gesendeten Daten.

Wichtige Anmerkung:
- Stellen Sie sicher, dass der Baudrate-Wert (in diesem Fall 9600) dem Baudrate-Wert entspricht, der in Ihrem Arduino-Code festgelegt ist.
- Das Programm verbindet sich direkt mit "COM5". Falls Ihr Arduino an einem anderen Port angeschlossen ist, ändern Sie den Port entsprechend im Code.
