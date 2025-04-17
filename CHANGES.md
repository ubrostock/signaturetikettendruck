Changelog und Migrationsanleitung für Signaturetikettendruck
============================================================

Version 1.4.0 (April 2025)
--------------------------
### Verbesserungen
- Update auf Java 17
- Code-Bereinigung (Verwendung von Java17 Features und Löschen veralteter Codebestandteile)
- Ergänzung weiterer Beispielkonfigurationen
- Hinzufügen einer **Ersetzungfunktion**
  - Über Properties können jetzt Zeichen innerhalb einer Zeile ersetzt werden,
    z.B. um Standort-Codes oder Fachsystematikstellen ausschreiben zu können.


Version 1.3.0 (Dezember 2022)
-----------------------------
### Verbesserungen
- Update auf Java 11
- Unterstützung der Konfiguration für mehrere Drucker
- Verbesserung der Ausdruckqualität (Vergrößerung des internen, vorgenerierten Bilds)


Version 1.2.1 (Januar 2020)
---------------------------
### Verbesserungen
Das Property `signed.application.1stbarcode` wurde hinzugefügt,
damit lässt sich der Barcode, der beim Programmstart automatisch angezeigt wird, konfigurieren.


Version 1.2.0 (März 2018)
-------------------------

### Änderung der Konfiguration
#### Reguläre Ausdrücke mit Named Groups
Für die Ermittlung der Textbausteine auf dem Etikett sollten jetzt **"Named Groups"**
(Feature in Java Regex) verwendet werden. Dafür wurde ein neues Property `*.regex`
für jedes Template eingeführt, welches den kompletten regulären Ausdruck enthält.

Vorher wurden auf die Zeichenkette schrittweise von links einzelnd konfigurierte Pattern angewendet, 
um dadurch die einzelnen Teilzeichenketten aus der Signatur für die Ersetzung in der SVG-Datei zu ermitteln.

Die Bezeichnungen der Names Groups dürfen keinen `_` enthalten. 
Deshalb müssen ggf. auch die ID-Attribute in den SVG-Dateien umbenannt werden. 

#### EingabeString enthält Standort und Signatur
Außerdem wird jetzt als Eingabe für den Regulären Ausdruck auch die komplette Zeichenkette
in der Form `!Standort!Signatur` verwendet. Dadurch können auch Standortinformationen auf 
dem Etikett gedruckt werden. Wird diese für das Etikett nicht benötigt, kann sie über den Regulären Ausdruck 
`([!].*[!])` identifiziert und ausgeschlossen werden.  

Alt:
```
signed.label.rvk.pattern.01.zeile_1=^[a-zA-Z]+
signed.label.rvk.pattern.02._ignore=\\s
signed.label.rvk.pattern.03.zeile_2=^[0-9]+([a-zA-Z0-9]+)?
signed.label.rvk.pattern.04._ignore=\\s
signed.label.rvk.pattern.05.zeile_3=^[-]?[^\\s-]+
signed.label.rvk.pattern.06._ignore=\\s
signed.label.rvk.pattern.07.zeile_4=^[-]?[^\\s-]+
signed.label.rvk.pattern.08._ignore=\\s
signed.label.rvk.pattern.09.zeile_5=.+ 
```

Neu:
```
signed.label.rvk.regex=^([!].*[!])(?<zeile1>[a-zA-Z]+)\\s(?<zeile2>[0-9]+([a-zA-Z0-9]+)?)(\\s?(?<zeile3>[-]?[^\\s-]+))?(\\s?(?<zeile4>[-]?[^\\s-]+))?(\\s?(?<zeile5>.+))?
```

Die Named Groups werden wie folgt definiert: `(?<name>...)`.
Für die Umstellung werden die alten regulären Ausdrücke aneinander gefügt und die Named Groups entsprechend gekennzeichnet.
Gegebenenfalls müssen die Teilausdrücke für die letzten Zeile als **optional** definiert werden: `(...)?`, 
da immer der gesamte reguläre Ausdruck auf die Signatur angewendet wird.

Die Konfiguration via `*.pattern` Properties wird in einer der nächsten Versionen entfernt.


### Anpassung der SVG-Dateien
1. Elemente, die nicht gedruckt werden (z.B. Ränder), sollten mit der Klasse 
`noprint` gekennzeichnet werden.
Bisher wurden diese Elemente mit ihrer ID im Property `*.noprint` eingetragen.
Diese Funktionalität wird in einer nächsten Version entfernt.

2. Die Teilzeichenketten können mit dem Attribut aria-label ausgezeichnet werden.
Dieses Attribut wird dann für die Anzeige des Feldes in der Eingabemaske verwendet.
Fehlt das Attribut wird, wie bisher, die ID des Elements für die Anzeige verwendet.


Version 1.1.1 (Mai 2015)
------------------------
Ersetzen der Konfigurationsdatei (signed_cfg.properties)


Version 1.1.0 (April 2015)
--------------------------

Auf Grund von Änderungen an den Etiketten für die Lehrerbildungsbibliothek müssen
wir die Anwendung aktualisieren.

Der Installationspfad ist "C:\Programmme\Etikettendruck"

1) Sichern der Datei "signed_cfg.properties vom Mitarbeiterrechner.

2) Ersetzen der Anwendung im Programmverzeichnis.

3) Übertragen der Drucker-Properties (am Dateiende nach "#Drucker-Konfiguration") 
   aus der Konfigurationsdatei der Mitarbeiter in 
   die NEUE Datei "signed_printer.properties" (überschreiben der bestehenden Einträge).
   Danach sollte alles wieder wie gewohnt funktionieren.
   
Version 1.0.0 (Oktober 2014)
----------------------------
Erste Version veröffentlicht.
Voraussetzung: Java 8