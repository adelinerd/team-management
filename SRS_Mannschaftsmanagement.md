# Software Requirements Specification – Vereinsweites Mannschaftsmanagement

**Version:** 2.2 – Mannschaftshistorie der Spieler und eindeutige aktive Zuordnung  
**Status:** Überarbeiteter Entwurf  
**Ausgangspunkt:** SRS „Mannschaftsmanagement E-Jugend“; bestehende REQ-IDs bleiben soweit möglich erhalten.

## 1. Ziel der Anwendung

Die Web-Anwendung unterstützt die Organisation **mehrerer Jugendfußballmannschaften eines Sportvereins**. Jede Mannschaft bildet einen logisch getrennten Mandanten innerhalb des Vereins. Ein zentraler Vereinsadministrator verwaltet die Mannschaften und ernennt deren Mannschaftsverantwortliche (Trainer). Mannschaftsverantwortliche organisieren ihre Mannschaft und können eine Elternkoordination für Trikotwäsche und Bewirtung ernennen.

Die Anwendung soll insbesondere folgende organisatorische Aufgaben vereinfachen:

- Vereinsweite Verwaltung von Mannschaften und deren Verantwortlichen
- Mannschaftsbezogene Verwaltung der Spiele, einschließlich CSV-Import
- Verwaltung von Spielern und Familien sowie Einladung von Eltern
- Organisation des Trikot-Waschplans
- Organisation der Bewirtung bei Heimspielen
- Selbstständige Eintragung der Familien
- Übersicht der übernommenen Aufgaben pro Familie

Die Anwendung soll sowohl auf Smartphones als auch auf Desktop-Geräten nutzbar sein.

## 2. Benutzerrollen und Berechtigungen

Die Rollen sind **nicht gegenseitig ausschließend**. Ein Benutzer kann innerhalb derselben Mannschaft zugleich Mannschaftsverantwortlicher (Trainer) und Elternteil sein. Derselbe Benutzer kann zudem in mehreren Mannschaften unterschiedliche Rollen besitzen. Es wird nur ein Benutzerkonto benötigt.

### 2.1 Vereinsadministrator (Club Admin)

Ein Vereinsadministrator darf:

- Mannschaften des Vereins anlegen, bearbeiten und deaktivieren
- pro Mannschaft bis zu drei Mannschaftsverantwortliche ernennen oder abberufen
- die Mannschaften und deren Zuständigkeiten überblicken
- Spieler vereinsweit anlegen und bestehenden Mannschaften zuordnen
- Spieler einzeln oder gesammelt zwischen Mannschaften versetzen, insbesondere zum Saisonwechsel
- bei Bedarf administrative Unterstützung leisten; solche Eingriffe sind zu protokollieren

Die Ernennung von Mannschaftsverantwortlichen ist dem Vereinsadministrator vorbehalten.

### 2.2 Mannschaftsverantwortlicher / Trainer (Team Manager)

Ein Mannschaftsverantwortlicher darf **innerhalb seiner Mannschaft**:

- Mannschaftsdaten pflegen
- Spieler und Familien verwalten sowie Eltern einladen
- Spiele anlegen, bearbeiten, löschen und per CSV importieren
- Heim- und Auswärtsspiele definieren
- Bewirtungslisten und Waschplan verwalten
- Eintragungen korrigieren oder entfernen
- eine Elternkoordination ernennen oder abberufen
- den Status aller Aufgaben der Mannschaft einsehen

Je Mannschaft dürfen höchstens **drei aktive Mannschaftsverantwortliche** ernannt sein.

### 2.3 Elternkoordination (Parent Coordinator)

Eine von einem Mannschaftsverantwortlichen ernannte Person darf innerhalb der betreffenden Mannschaft:

- Waschaufgaben und deren Zuordnungen organisieren
- Bewirtungslisten erstellen, bearbeiten und aus früheren Heimspielen kopieren
- Familienzuordnungen zu Wasch- und Bewirtungsaufgaben korrigieren
- den Status der organisatorischen Aufgaben einsehen

Die Elternkoordination darf keine Mannschaftsverantwortlichen ernennen, keine Mannschaften anlegen und keine Spieler- oder Familienmitgliedschaften administrieren.

### 2.4 Eltern / Familie

Ein berechtigter Elternteil darf:

- die Spiele der zugeordneten Mannschaft(en) sehen
- die eigene Familie für Trikotwäsche und Bewirtung ein- oder austragen
- eigene Familienaufgaben einsehen
- bei mehreren Mannschaftszuordnungen zwischen Mannschaften wechseln und eigene Aufgaben mannschaftsübergreifend sehen

Die Eintragung erfolgt **für eine Familie**, nicht für ein einzelnes Benutzerkonto.

### 2.5 Rollenüberlagerung

- Ein Benutzer darf mehrere Rollen in derselben Mannschaft besitzen.
- Insbesondere darf ein Mannschaftsverantwortlicher zugleich Elternteil derselben Mannschaft sein und Aufgaben für seine Familie übernehmen.
- Eine Elternkoordination darf zugleich Elternteil oder Mannschaftsverantwortlicher sein.
- Die Rolle „Elternteil“ setzt eine Zuordnung zu einer Familie voraus; die Trainerrolle allein begründet keine Familienzuordnung.
- Rollen gelten im Kontext der jeweiligen Mannschaft. Vereinsweite Rechte gelten nur für den Vereinsadministrator.
- Die Ernennung oder Abberufung einer Rolle verändert weder die Familienmitgliedschaft noch bestehende Aufgabenübernahmen.

## 3. Verein, Mannschaften und Mandantentrennung

### REQ-CLUB-001 – Vereinsdaten

Das System muss einen Verein mit Name und einer eindeutigen Kennung verwalten können. Ein Verein kann mehrere Mannschaften besitzen.

### REQ-CLUB-002 – Mannschaften verwalten

Ein Vereinsadministrator muss Mannschaften anlegen, bearbeiten und deaktivieren können.

### REQ-TEAM-001 – Mannschaftsdaten

Das System soll für jede Mannschaft mindestens folgende Daten speichern:

- eindeutige Mannschaftskennung
- Mannschaftsname (z. B. E1, E2, E3)
- Verein
- Saison
- optionale Beschreibung
- Status (aktiv/inaktiv)

Beispiel: **SVE E3 – Saison 2026/2027**.

### REQ-TEAM-002 – Mannschaft als logischer Mandant

Spiele, Mannschaftsmitgliedschaften, Spielerzuordnungen, Waschaufgaben und Bewirtungsdaten müssen einer Mannschaft eindeutig zugeordnet sein. Benutzer dürfen nur Daten der Mannschaften lesen oder ändern, für die sie berechtigt sind. Vereinsadministratoren besitzen gesondert geregelte übergreifende Rechte.

### REQ-TEAM-003 – Mannschaftsverantwortliche ernennen

Ein Vereinsadministrator muss bestehende Benutzer zu Mannschaftsverantwortlichen ernennen und diese Ernennung widerrufen können.

### REQ-TEAM-004 – Höchstens drei Trainer

Pro Mannschaft dürfen höchstens drei **aktive** Mannschaftsverantwortliche vorhanden sein. Diese Grenze muss serverseitig auch bei gleichzeitigen Ernennungen eingehalten werden.

### REQ-TEAM-005 – Elternkoordination ernennen

Ein Mannschaftsverantwortlicher muss eine bestehende, der Mannschaft zugeordnete Person zur Elternkoordination ernennen und die Ernennung widerrufen können.

Für die erste Version ist **eine aktive Elternkoordination pro Mannschaft** vorgesehen. Eine spätere Erweiterung auf mehrere Koordinatoren bleibt möglich.

### REQ-TEAM-006 – Mannschaftswechsel

Benutzer mit Mitgliedschaften in mehreren Mannschaften müssen die aktive Mannschaft wechseln können, ohne sich erneut anzumelden.

### REQ-TEAM-007 – Mehrere Rollen pro Mannschaft

Das System muss einem Benutzer mehrere Rollen in derselben Mannschaft zuordnen können. Die Rechte ergeben sich aus der Kombination der Rollen.

### REQ-TEAM-008 – Ernennungen nachvollziehen

Das System muss Ernennung und Abberufung von Mannschaftsverantwortlichen und Elternkoordination mit ausführender Person und Zeitpunkt protokollieren.

## 4. Spieler-, Familienverwaltung und Einladungen

### REQ-FAM-001 – Familie anlegen

Ein Mannschaftsverantwortlicher muss eine Familie erfassen und seiner Mannschaft zuordnen können. Eine Familie soll mindestens einen Familiennamen besitzen; Ansprechpartner und optionale Kontakt-E-Mail können ergänzt werden. Die Zuordnung von Kindern erfolgt über eigenständige Spielerdatensätze.

### REQ-FAM-002 – Familie bearbeiten

Ein Mannschaftsverantwortlicher muss Familien- und Zuordnungsdaten innerhalb seiner Mannschaft bearbeiten können.

### REQ-FAM-003 – Familie deaktivieren

Ein Mannschaftsverantwortlicher muss eine Familienzuordnung zur Mannschaft deaktivieren können. Deaktivierte Zuordnungen dürfen keine neuen Aufgaben übernehmen; vorhandene Eintragungen bleiben für die Nachvollziehbarkeit erhalten.

### REQ-FAM-004 – Mehrere Elternteile pro Familie

Eine Familie muss mit mehreren Benutzerkonten verknüpft werden können. Alle zugehörigen Elternteile sehen die Aufgaben dieser Familie; ein Elternteil kann die Familie bei der Aufgabenübernahme vertreten.

### REQ-FAM-005 – Familien in mehreren Mannschaften

Eine Familie darf mit mehreren Mannschaften verknüpft sein, beispielsweise wenn Geschwister in verschiedenen Teams spielen. Aufgaben bleiben der jeweiligen Mannschaft und der Familie zugeordnet.

### REQ-PLAYER-001 – Spieler verwalten

Ein Mannschaftsverantwortlicher muss Spieler innerhalb seiner Mannschaft anlegen, bearbeiten und deaktivieren können. **Auch der Vereinsadministrator muss Spieler vereinsweit anlegen, bearbeiten und einer Mannschaft zuordnen können.** Für einen Spieler ist mindestens ein Name erforderlich; die Mannschaftszuordnung wird getrennt und saisonbezogen gespeichert.

### REQ-PLAYER-002 – Spieler und Familie verknüpfen

Ein Spieler muss einer Familie zugeordnet werden können. Mehrere Spieler können derselben Familie angehören.

### REQ-PLAYER-003 – Vereinsweite Spielerübersicht

Der Vereinsadministrator muss alle Spieler des Vereins über Mannschaften und Saisons hinweg einsehen und nach Mannschaft, Saison und Namen filtern können.

### REQ-PLAYER-004 – Spieler einer Mannschaft zuordnen

Der Vereinsadministrator muss einen Spieler einer Mannschaft und Saison zuordnen können, ohne einen neuen Spielerdatensatz anzulegen. Pro Spieler darf zu jedem Zeitpunkt höchstens **eine aktive Mannschaftszuordnung** bestehen. Eine neue aktive Zuordnung muss eine bisherige aktive Zuordnung beenden.

### REQ-PLAYER-005 – Spieler in eine andere Mannschaft versetzen

Der Vereinsadministrator muss einen oder mehrere Spieler von einer Quellmannschaft in eine Zielmannschaft versetzen können. Die Vorschau muss Spieler, Quellmannschaft mit Saison und Zielmannschaft mit Saison eindeutig ausweisen. Der Wechsel darf erst nach ausdrücklicher Bestätigung ausgeführt werden; Abbrechen darf keine Änderungen verursachen.

### REQ-PLAYER-006 – Saisonwechsel und Sammelversetzung

Der Vereinsadministrator muss mehrere ausgewählte Spieler gesammelt in eine Mannschaft der neuen Saison versetzen können. Vor der Ausführung muss das System die Anzahl und Namen der Spieler, Quell- und Zielmannschaft sowie die jeweiligen Saisons anzeigen und eine ausdrückliche Bestätigung verlangen. Ungültige oder widersprüchliche Zuordnungen müssen vor der Ausführung angezeigt werden. Die Übernahme muss transaktional erfolgen: entweder alle ausgewählten Spieler werden versetzt oder keiner.

### REQ-PLAYER-007 – Mannschaftshistorie des Spielers

Das System muss für jeden Spieler speichern, welchen Mannschaften er in welchen Saisons angehört hat. Beim Wechsel wird die bisherige aktive Zuordnung beendet und eine neue aktive Zuordnung angelegt. Frühere Zuordnungen bleiben als **Mannschaftshistorie** erhalten und sind im Spielerprofil chronologisch einsehbar. Die Historie umfasst mindestens Mannschaft und Saison sowie die Kennzeichnung der aktuellen Zuordnung. Eine separate Historie der organisatorischen Familienaufgaben gehört ausdrücklich **nicht** zur Mannschaftshistorie des Spielers.

### REQ-PLAYER-008 – Spieleridentität erhalten

Ein Mannschaftswechsel darf keinen doppelten Spielerdatensatz erzeugen. Die eindeutige Spielerkennung und die Verbindung zur Familie müssen erhalten bleiben.

### REQ-PLAYER-009 – Familienzugang nach Mannschaftswechsel

Nach einer Versetzung muss das System die Zugriffsberechtigungen der zugehörigen Eltern für die Zielmannschaft prüfen und bei Bedarf eine entsprechende Mannschaftszuordnung oder Einladung vorbereiten. Der Wechsel darf keine unberechtigten Zugriffe auf andere Mannschaften ermöglichen. Ein Spielerwechsel überträgt keine Wasch- oder Bewirtungsaufgaben der Familie auf die Zielmannschaft.

### REQ-PLAYER-010 – Keine separate Transfer- oder Aufgabenhistorie

Die Mannschaftshistorie wird ausschließlich aus den gespeicherten Mannschaftszuordnungen des Spielers abgeleitet. Ein gesondertes fachliches Transferprotokoll mit ausführender Person und Änderungszeitpunkt ist für den MVP nicht erforderlich. Wasch- und Bewirtungsaufgaben bleiben eigenständige, spielbezogene Datensätze und werden nicht im Spielerprofil als Historie angezeigt. Gesetzlich oder technisch erforderliche Sicherheitsprotokollierung bleibt davon unberührt.

### REQ-PLAYER-011 – Genau eine aktive Mannschaft

Ein Spieler darf zu jedem Zeitpunkt **höchstens einer aktiven Mannschaft** zugeordnet sein, auch wenn er in vergangenen Saisons mehreren Mannschaften angehört hat. Das System muss diese Regel serverseitig und durch geeignete Datenbankbedingungen gegen parallele Schreibzugriffe absichern. Ein Spieler ohne aktive Zuordnung ist zulässig, etwa zwischen Saisons.

### REQ-PLAYER-012 – Mannschaftshistorie im Spielerprofil

Die Vereinsadministration muss im Spielerprofil die aktuelle Mannschaft und die vergangenen Mannschaften mit ihren Saisons anzeigen. Ein Wechsel darf die Historie nicht überschreiben oder entfernen.

### REQ-PLAYER-013 – Unabhängigkeit von Familienaufgaben

Bei einer Versetzung dürfen Wasch- und Bewirtungsaufgaben nicht automatisch in die neue Mannschaft kopiert oder übertragen werden. Ihre Verwaltung und Aufbewahrung richten sich nach den allgemeinen Regeln für Spiele und Familienaufgaben; sie sind keine Bestandteile der Spieler-Mannschaftshistorie.

### REQ-INV-001 – Eltern einladen

Ein Mannschaftsverantwortlicher muss eine Einladung an eine E-Mail-Adresse auslösen können, um einen Elternteil einer Mannschaft und einer Familie zuzuordnen.

### REQ-INV-002 – Einladung annehmen

Ein eingeladener Elternteil muss sich registrieren oder mit einem bestehenden Konto anmelden und die Einladung annehmen können. Erst danach erhält er Zugriff auf die entsprechenden Familien- und Mannschaftsdaten.

### REQ-INV-003 – Einladung absichern

Einladungen müssen zeitlich begrenzt, eindeutig und nach erfolgreicher Annahme nicht erneut verwendbar sein. Abgelaufene oder widerrufene Einladungen dürfen keinen Zugang gewähren.

### REQ-INV-004 – Einladungsstatus

Mannschaftsverantwortliche müssen den Status von Einladungen (offen, angenommen, abgelaufen, widerrufen) sehen und offene Einladungen widerrufen können.

## 5. Spielverwaltung

### REQ-GAME-001 – Spiel anlegen

Ein Mannschaftsverantwortlicher soll ein Spiel anlegen können.

Ein Spiel soll mindestens folgende Daten enthalten:

- Datum
- Uhrzeit
- Gegner
- Heimspiel oder Auswärtsspiel
- optional Spielort
- optional Bemerkung

### REQ-GAME-002 – Spiel bearbeiten

Ein Mannschaftsverantwortlicher soll ein vorhandenes Spiel bearbeiten können.

### REQ-GAME-003 – Spiel löschen

Ein Mannschaftsverantwortlicher soll ein Spiel löschen können. Vor dem Löschen soll eine Bestätigung erfolgen.

### REQ-GAME-004 – Spielliste

Das System soll alle Spiele chronologisch darstellen.

Dabei soll erkennbar sein:

- Datum
- Uhrzeit
- Gegner
- Heimspiel/Auswärtsspiel
- Trikotwäsche vergeben/nicht vergeben
- bei Heimspielen: Status der Bewirtung

### REQ-GAME-005 – Vergangene Spiele

Vergangene Spiele sollen weiterhin einsehbar sein. Sie sollen visuell von kommenden Spielen unterschieden werden.

### REQ-GAME-006 – Mannschaftszuordnung

Jedes Spiel muss genau einer Mannschaft zugeordnet sein. Mannschaftsverantwortliche dürfen nur Spiele ihrer eigenen Mannschaft bearbeiten.

### REQ-GAME-007 – CSV-Spielimport

Ein Mannschaftsverantwortlicher muss mehrere Spiele aus einer CSV-Datei importieren können. Die Anwendung muss eine Vorschau, Feldzuordnung und Validierung anbieten. Fehlerhafte Zeilen müssen ausgewiesen werden; gültige Zeilen sollen unabhängig davon importiert werden können.

### REQ-GAME-008 – Duplikate und bestehende Aufgaben

Vor dem Import muss das System mögliche doppelte Spiele kennzeichnen. Ein erneuter Import darf vorhandene Wasch- und Bewirtungszuordnungen nicht stillschweigend löschen oder überschreiben.


## 6. Trikot-Waschplan

### REQ-WASH-001 – Waschaufgabe pro Spiel

Für jedes Spiel soll eine Aufgabe **„Trikots waschen“** existieren.

### REQ-WASH-002 – Familie eintragen

Ein berechtigter Elternteil soll seine der Mannschaft zugeordnete Familie für die Trikotwäsche eines Spiels eintragen können.

### REQ-WASH-003 – Nur eine Familie pro Spiel

Für ein Spiel darf nur eine Familie für die Trikotwäsche eingetragen sein. Diese Eindeutigkeit muss auch bei gleichzeitigen Eintragungsversuchen serverseitig gewährleistet werden.

### REQ-WASH-004 – Belegte Aufgabe anzeigen

Ist die Trikotwäsche bereits vergeben, soll angezeigt werden, welche Familie die Aufgabe übernommen hat.

Beispiel: **Trikotwäsche: Familie Müller**

### REQ-WASH-005 – Eintragung zurücknehmen

Eine Familie soll ihre eigene Eintragung wieder zurücknehmen können. Danach soll die Aufgabe wieder als frei angezeigt werden.

### REQ-WASH-006 – Eintragung durch Administrator ändern

Ein Mannschaftsverantwortlicher oder die Elternkoordination soll eine vorhandene Wasch-Eintragung ändern oder entfernen können.

### REQ-WASH-007 – Status

Die Anwendung soll den Status einer Waschaufgabe anzeigen.

Mögliche Zustände:

- Frei
- Vergeben
- Erledigt

### REQ-WASH-008 – Erledigt markieren

Die eingetragene Familie, ein Mannschaftsverantwortlicher oder die Elternkoordination soll eine Waschaufgabe als erledigt markieren können.

## 7. Bewirtung

Die Bewirtungsfunktion wird ausschließlich für Heimspiele benötigt.

### REQ-CATER-001 – Bewirtung nur bei Heimspielen

Für Auswärtsspiele soll keine Bewirtungsliste angezeigt werden.

### REQ-CATER-002 – Bewirtung aktivieren

Ein Mannschaftsverantwortlicher oder die Elternkoordination soll für ein Heimspiel eine Bewirtung aktivieren können.

### REQ-CATER-003 – Einträge erstellen

Ein Mannschaftsverantwortlicher oder die Elternkoordination soll beliebig viele Bewirtungsposten für ein Heimspiel erstellen können.

Beispiele:

- 2 × Kuchen
- 2 × Kaffee
- 3 × Wasser
- 2 × Brezeln
- 1 × Obst
- 2 Personen für Verkauf
- 1 Person für Aufbau
- 1 Person für Abbau

### REQ-CATER-004 – Anzahl definieren

Für einen Bewirtungsposten soll definiert werden können, wie viele Beiträge benötigt werden.

Beispiel: **Kuchen – benötigt: 3**

### REQ-CATER-005 – Familie eintragen

Ein berechtigter Elternteil soll seine der Mannschaft zugeordnete Familie für einen freien Bewirtungsposten eintragen können.

### REQ-CATER-006 – Mehrere Familien pro Posten

Wenn für einen Posten mehrere Beiträge benötigt werden, sollen sich entsprechend mehrere Familien eintragen können. Dieselbe Familie darf denselben Posten nur einmal belegen; parallele Eintragungen dürfen die Kapazität nicht überschreiten.

Beispiel:

**Kuchen – 3 benötigt**

- Familie Müller
- Familie Schmidt
- Frei

### REQ-CATER-007 – Vollständige Belegung

Sind alle benötigten Plätze eines Bewirtungspostens vergeben, soll dieser als vollständig belegt angezeigt werden.

### REQ-CATER-008 – Eigene Eintragung entfernen

Eine Familie soll ihre eigene Bewirtungseintragung wieder entfernen können.

### REQ-CATER-009 – Eintragung durch Administrator ändern

Ein Mannschaftsverantwortlicher oder die Elternkoordination soll Bewirtungseintragungen korrigieren oder entfernen können.

### REQ-CATER-010 – Bewirtungsstatus des Spiels

Das System soll für ein Heimspiel anzeigen, ob die Bewirtung vollständig organisiert ist.

Mögliche Zustände:

- Noch keine Eintragungen
- Teilweise besetzt
- Vollständig besetzt

### REQ-CATER-011 – Bewirtungsliste aus vorherigem Spiel kopieren

Ein Mannschaftsverantwortlicher oder die Elternkoordination soll die Bewirtungsliste eines Heimspiels als Vorlage für ein anderes Heimspiel übernehmen können, indem er sie aus einem vorherigen Spiel kopiert.

Die kopierten Bewirtungsposten sollen eigenständige Datensätze ohne Verknüpfung zum Ursprungsspiel sein. Änderungen an der Kopie (z. B. Bezeichnung, Anzahl, Löschen einzelner Posten) sollen keine Auswirkung auf die ursprüngliche Bewirtungsliste haben, und umgekehrt.

Bereits vorhandene Eintragungen von Familien (REQ-CATER-005) sollen beim Kopieren **nicht** übernommen werden – die kopierten Posten starten unbelegt.

## 8. Familienübersicht

### REQ-OVERVIEW-001 – Meine Aufgaben

Eine Familie soll eine Übersicht aller eigenen Aufgaben erhalten. Elternteile, die mehreren Mannschaften angehören, sollen diese Aufgaben auch mannschaftsübergreifend sehen können.

Die Übersicht soll mindestens enthalten:

- Datum
- Spiel
- Gegner
- Art der Aufgabe
- Beschreibung
- zugehörige Mannschaft

### REQ-OVERVIEW-002 – Aufgabenarten

Folgende Aufgaben sollen mindestens unterschieden werden:

- Trikotwäsche
- Bewirtung / Spende
- Bewirtung / Helferdienst

### REQ-OVERVIEW-003 – Kommende Aufgaben

Kommende Aufgaben sollen besonders hervorgehoben werden.

### REQ-OVERVIEW-004 – Vergangene Aufgaben

Vergangene Aufgaben sollen weiterhin einsehbar sein.

## 9. Dashboard

### REQ-DASH-001 – Startseite

Nach dem Öffnen der Anwendung soll eine übersichtliche Startseite angezeigt werden.

Sie soll mindestens enthalten:

- nächstes Spiel
- Heim-/Auswärtsspiel
- Gegner
- Datum und Uhrzeit
- Status Trikotwäsche
- Status Bewirtung bei Heimspielen

### REQ-DASH-002 – Offene Aufgaben

Die Startseite soll anzeigen, für welche kommenden Spiele noch Aufgaben offen sind.

Beispiele:

- Trikotwäsche noch frei
- 2 Kuchen fehlen
- 1 Helfer für Verkauf fehlt

### REQ-DASH-003 – Meine nächsten Aufgaben

Angemeldete Elternteile sollen die nächsten Aufgaben ihrer Familie direkt auf der Startseite sehen können. Bei Mitgliedschaft in mehreren Mannschaften muss die Mannschaft erkennbar sein.

## 10. Anmeldung, Benutzerkonten und Berechtigungen

### REQ-AUTH-001 – Anmeldung

Benutzer müssen sich an der Anwendung anmelden können.

### REQ-AUTH-002 – Ein Benutzerkonto für mehrere Mannschaften

Ein Benutzerkonto muss mit mehreren Mannschaften und gegebenenfalls mehreren Familienzuordnungen verknüpft werden können. Es darf keine erneute Registrierung pro Mannschaft erforderlich sein.

### REQ-AUTH-003 – Mehrere Benutzer pro Familie

Mehrere Benutzerkonten müssen derselben Familie zugeordnet werden können. Diese Benutzer sehen dieselben Familienaufgaben.

### REQ-AUTH-004 – Rollenbasierte Berechtigungen

Das System muss Berechtigungen für Vereinsadministrator, Mannschaftsverantwortliche, Elternkoordination und Elternteil unterscheiden. Ein Benutzer kann mehrere Rollen gleichzeitig besitzen. Die Berechtigungen müssen auf dem Server überprüft werden.

### REQ-AUTH-005 – Teambezogene Zugriffskontrolle

Bei jeder Lese- und Schreiboperation auf mannschaftsbezogenen Daten muss die Berechtigung für die betroffene Mannschaft geprüft werden. Eine frei übergebene Mannschaftskennung darf keinen Zugriff auf fremde Mannschaften ermöglichen.

### REQ-AUTH-006 – Trennung von Rolle und Familienzuordnung

Die Rollen eines Benutzers und dessen Zuordnung zu einer Familie müssen unabhängig verwaltet werden. Ein Mannschaftsverantwortlicher darf nur dann Aufgaben als Familie übernehmen, wenn er tatsächlich einer berechtigten Familie der Mannschaft zugeordnet ist.

### REQ-AUTH-007 – Mehrfachrollen innerhalb derselben Mannschaft

Ein Benutzer muss in derselben Mannschaft gleichzeitig Mannschaftsverantwortlicher und Elternteil sein können; ebenso sind andere zulässige Rollenkombinationen möglich.

## 11. Bedienbarkeit

### REQ-UI-001 – Responsive Design

Die Anwendung soll auf folgenden Geräten nutzbar sein:

- Smartphone
- Tablet
- Desktop-PC

Die Smartphone-Darstellung soll für das Elternportal priorisiert werden. Die Verwaltungsoberflächen sollen insbesondere auf Desktop und Tablet gut bedienbar sein.

### REQ-UI-002 – Einfache Eintragung

Eine Familie soll sich mit möglichst wenigen Aktionen für eine Aufgabe eintragen können.

### REQ-UI-003 – Status visuell darstellen

Freie, teilweise belegte und vollständig belegte Aufgaben sollen visuell unterscheidbar sein.

### REQ-UI-005 – Getrennte Arbeitsbereiche

Das System soll klar getrennte Oberflächen für Vereinsadministration, Mannschaftsverwaltung und Elternportal anbieten. Diese können technisch als getrennte Bereiche einer React-Anwendung realisiert werden.

### REQ-UI-006 – Kontextwechsel

Benutzer mit mehreren Mannschaftszuordnungen müssen ihre aktive Mannschaft eindeutig erkennen und wechseln können. Benutzer mit Verwaltungs- und Elternrechten sollen zwischen Verwaltungs- und Familienansicht wechseln können, ohne sich neu anzumelden.

### REQ-UI-004 – Bestätigung

Nach einer erfolgreichen Eintragung soll eine Bestätigung angezeigt werden.

Beispiel: **Du bist für die Trikotwäsche am 12.09. eingetragen.**

## 12. Benachrichtigungen – optionale Erweiterung

### REQ-NOTIFY-001 – Erinnerung vor Aufgabe

Das System sollte einer Familie vor einer übernommenen Aufgabe eine Erinnerung senden können.

Beispiel: **Morgen bist du für die Trikotwäsche eingetragen.**

### REQ-NOTIFY-002 – Erinnerung an Bewirtung

Das System sollte vor einem Heimspiel an übernommene Bewirtungsaufgaben erinnern können.

### REQ-NOTIFY-003 – Offene Aufgaben

Optional soll das System vor einem Heimspiel auf noch offene Bewirtungsaufgaben hinweisen können.

## 13. Administration und Import

### REQ-ADMIN-001 – Saison anlegen

Ein Vereinsadministrator soll eine neue Saison verwalten können. Mannschaftsverantwortliche sollen ihre Mannschaft der freigegebenen Saison zuordnen und deren saisonbezogene Daten pflegen können.

### REQ-ADMIN-002 – Daten vorheriger Saison

Spiele und Aufgaben vergangener Saisons sollen archiviert werden können.

### REQ-ADMIN-003 – Familien übernehmen

Familien- und Spielerzuordnungen sollen optional aus einer vorherigen Saison übernommen werden können. Rollen und Einladungsstatus müssen dabei überprüft werden.

### REQ-ADMIN-004 – Datenimport aus Datei

Ein Mannschaftsverantwortlicher soll Mannschaftsdaten aus JSON- oder CSV-Dateien importieren können, insbesondere:

- Spiele
- Bewirtungsposten
- Familien und Spieler

Vor der Übernahme sollen die Datensätze angezeigt werden. Fehlerhafte oder unvollständige Datensätze müssen erkennbar sein und dürfen den Import der übrigen gültigen Datensätze nicht verhindern.

### REQ-ADMIN-005 – Vereinsadministration

Die Vereinsadministration muss eine Übersicht aller Mannschaften, der zugeordneten Mannschaftsverantwortlichen und der vereinsweit registrierten Spieler anbieten. Sie muss Spieler hinzufügen, zuordnen und zwischen Mannschaften versetzen können.

### REQ-ADMIN-007 – Saisonwechselassistent

Die Vereinsadministration soll einen Saisonwechselassistenten anbieten, der den Kader einer Quellmannschaft anzeigt und die Zuordnung ausgewählter Spieler zu Mannschaften der neuen Saison ermöglicht. Die Übernahme muss vor der Ausführung geprüft und bestätigt werden. Die Zuordnung von Trainern und Elternkoordination wird dabei nicht automatisch übertragen. Für jeden Spieler wird die bisherige aktive Mannschaftszuordnung abgeschlossen und die neue aktiviert; eine Vorschau und ausdrückliche Bestätigung sind erforderlich.

### REQ-ADMIN-006 – Mannschaftsverwaltung

Die Mannschaftsverwaltung muss getrennte Bereiche für Spiele, Spieler, Familien, Einladungen, Elternkoordination sowie Wasch- und Bewirtungsorganisation anbieten.

## 14. Datenschutz

### REQ-SEC-001 – Zugriffsschutz

Nicht angemeldete Benutzer dürfen keine personenbezogenen Daten der Familien sehen. Auch angemeldete Benutzer dürfen nur die Daten einsehen, für die sie innerhalb ihrer Mannschaft(en) berechtigt sind.

### REQ-SEC-002 – Minimale personenbezogene Daten

Es sollen nur die für die Organisation notwendigen personenbezogenen Daten gespeichert werden.

### REQ-SEC-003 – Eigene Daten

Berechtigte Elternteile sollen ihre eigenen Familien- und Zuordnungsdaten einsehen können.

## 15. MVP – Empfohlener Umfang für Version 1

Für eine erste vereinsweite Version sollten folgende Funktionen umgesetzt werden:

1. Vereinsadministrator verwaltet mehrere Mannschaften und kann Spieler vereinsweit hinzufügen, zuordnen und zwischen Mannschaften versetzen.
2. Vereinsadministrator ernennt maximal drei Mannschaftsverantwortliche je Mannschaft.
3. Vereinsadministrator kann Spieler zum Saisonwechsel einzeln oder gesammelt einer neuen Mannschaft zuordnen; Spieleridentität und Mannschaftshistorie bleiben erhalten; es gibt höchstens eine aktive Mannschaft pro Spieler.
4. Mannschaftsverantwortliche ernennen eine Elternkoordination.
5. Benutzer können mehrere Rollen innerhalb derselben Mannschaft besitzen, insbesondere Trainer und Elternteil.
6. Spieler, Familien und Elternzuordnungen mit Einladungsprozess.
7. Ein Benutzerkonto für mehrere Mannschaften; Mannschaftswechsel.
8. Spielplan je Mannschaft mit Heim-/Auswärtsspiel und CSV-Import.
9. Trikot-Waschplan mit genau einer Familie pro Spiel.
10. Bewirtungslisten und eigenständige Aufgabenübernahme durch Familien.
11. „Meine Aufgaben“ mit Mannschaftsangabe und optionaler teamübergreifender Ansicht.
12. Eltern-Dashboard mit offenen Aufgaben.
13. Getrennte Oberflächen für Vereinsadministrator, Mannschaftsverwaltung und Elternportal.
14. Serverseitige rollen- und mannschaftsbezogene Zugriffskontrolle.

Benachrichtigungen, automatisierte Saisonarchivierung und weitergehende Integrationen können später ergänzt werden.

## 16. Mögliche Hauptseiten der Anwendung

### 16.1 Elternportal (mobile-first)

- Dashboard
- Spiele (mit Mannschaftsfilter)
- Trikotwäsche
- Bewirtung
- Meine Aufgaben (optional mannschaftsübergreifend)
- Profil / Mannschaftsauswahl

### 16.2 Mannschaftsverwaltung (Desktop-/Tablet-orientiert)

- Mannschaftsübersicht
- Spielverwaltung und CSV-Import
- Spieler und Familien
- Eltern einladen / Einladungsstatus
- Elternkoordination ernennen / abberufen
- Waschplan und Bewirtungslisten verwalten

### 16.3 Vereinsadministration (Desktop-orientiert)

- Vereinsübersicht
- Mannschaften anlegen / deaktivieren
- Mannschaftsverantwortliche ernennen / abberufen
- Vereinsweite Spielerübersicht und Spieler anlegen
- Spieler einzeln oder gesammelt in andere Mannschaften versetzen
- Saisonwechselassistent mit Vorschau
- Saisonverwaltung

## 17. Fachliches Datenmodell (konzeptionell)

Folgende Entitäten sind vorgesehen:

- **Club:** Verein als organisatorischer Rahmen
- **Team:** Mannschaft mit Vereins- und Saisonzuordnung
- **User:** Benutzerkonto mit eindeutiger Identität
- **TeamMembership:** Mitgliedschaft eines Benutzers in einer Mannschaft
- **TeamMembershipRole:** eine oder mehrere Rollen einer Mannschaftsmitgliedschaft
- **Family:** Familie als Trägerin von Aufgabenübernahmen
- **FamilyMember:** Zuordnung von Benutzern zu einer Familie
- **Player:** Vereinsweit eindeutiger Spieler mit Familienzuordnung
- **PlayerTeamMembership:** Mannschafts- und Saisonzuordnung eines Spielers; frühere Zuordnungen bilden die Mannschaftshistorie, höchstens eine ist aktiv
- **Invitation:** Einladung eines Elternteils zu Mannschaft/Familie
- **Match:** Spiel einer Mannschaft
- **WashingAssignment:** Familienzuordnung zu einer Waschaufgabe eines Spiels
- **CateringTask:** Bewirtungsposten mit benötigter Anzahl
- **CateringAssignment:** Zuordnung einer Familie zu einem Bewirtungsposten

Wichtige fachliche Integritätsbedingungen:

- Maximal drei aktive Mannschaftsverantwortliche je Mannschaft.
- Für die erste Version maximal eine aktive Elternkoordination je Mannschaft.
- Genau eine Waschaufgabe pro Spiel und maximal eine zugeordnete Familie.
- Ein Bewirtungsposten darf nicht über die benötigte Anzahl hinaus belegt werden.
- Familien dürfen nur Aufgaben einer Mannschaft übernehmen, der sie zugeordnet sind.
- Benutzerrollen und Familienmitgliedschaften werden getrennt gespeichert.
- Aufgaben und deren Zuordnungen bleiben eindeutig der jeweiligen Mannschaft zugeordnet.
- Pro Spieler existiert höchstens eine aktive Mannschaftszuordnung; abgeschlossene Zuordnungen bleiben als Mannschaftshistorie erhalten.
- Wasch- und Bewirtungsaufgaben sind keine Bestandteile der Spielerhistorie und werden bei einem Mannschaftswechsel nicht übertragen.
- Eine Sammelversetzung erfolgt atomar und nur nach ausdrücklicher Bestätigung.
- Die vereinsweite Spielerkennung bleibt über Saison- und Mannschaftswechsel stabil.

## 18. Offene Entscheidungen

Die folgenden Punkte sind in dieser Überarbeitung als Annahmen bzw. noch zu klärende Entscheidungen gekennzeichnet:

1. **Elternkoordination:** Vorerst eine aktive Person pro Mannschaft; bei Bedarf später mehrere.
2. **Vereinsadministrator:** Ein oder mehrere Konten mit Vereinsadministratorrechten?
3. **Saisonmodell:** Mannschaften als dauerhaftes Objekt mit saisonbezogenen Kadern oder neue Mannschaftsinstanzen je Saison?
4. **Familienzuordnung:** Können Eltern mehreren Familien zugeordnet sein (z. B. getrennte Haushalte)?
5. **Import:** Konkretes CSV-Schema und Regeln für Aktualisierung bestehender Spiele.
6. **Einladungen:** Gültigkeitsdauer, erneutes Versenden und Behandlung von E-Mail-Adressen ohne vorhandenes Konto.
7. **Aufgabenhistorie:** Ob Änderungen an bereits vergangenen Aufgaben weiterhin erlaubt sein sollen.
8. **Technische Umsetzung:** Eine React-Anwendung mit drei Arbeitsbereichen oder getrennte Frontends mit gemeinsamem Backend.
10. **Saisonwechsel:** Soll eine Versetzung automatisch Elternzugänge zur Zielmannschaft aktivieren oder zunächst eine Bestätigung/Einladung erfordern?
11. **Aktuelle Saison:** Soll die Versetzung innerhalb einer laufenden Saison zulässig sein, oder nur für zukünftige Saisons?

