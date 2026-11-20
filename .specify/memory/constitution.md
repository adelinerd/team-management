<!--
Sync Impact Report
Version change: 1.0.0 -> 1.1.0
Modified principles: none
Modified sections: Additional Constraints; Development Workflow and Quality Gates
Added sections: none
Removed sections: none
Follow-up TODO: confirm the original ratification date with the project owner.
Follow-up TODO: align container setup and test artifacts with Podman Desktop and
  validate them once Podman Desktop is installed.
-->

# Mannschaftsmanagement Constitution

## Core Principles

### 1. Team-Scoped Authorization and Least Privilege
Jede serverseitige Lese- und Schreiboperation auf mannschaftsbezogene Daten MUSS die
Berechtigung des angemeldeten Benutzers für genau diese Mannschaft prüfen. Eine vom
Client übermittelte Mannschaftskennung ist kein Berechtigungsnachweis. Vereinsweite
Rechte sind auf ausdrücklich definierte Vereinsadministration beschränkt; Rollen und
Familienzugehörigkeiten MÜSSEN getrennt verwaltet werden. So wird verhindert, dass
Rollenüberlagerungen oder manipulierte Kennungen Zugriff auf fremde Mannschaftsdaten
gewähren.

### 2. Privacy by Default
Es DÜRFEN nur personenbezogene Daten gespeichert und angezeigt werden, die für die
Vereins- und Mannschaftsorganisation erforderlich sind. Nicht angemeldete oder nicht
autorisierte Benutzer DÜRFEN keine Familien- oder Spielerdaten erhalten. Einladungen
MÜSSEN eindeutig, zeitlich begrenzt und nach Annahme nicht wiederverwendbar sein.
Administrative Eingriffe, die Mitgliedschaften oder Ernennungen ändern, MÜSSEN
nachvollziehbar protokolliert werden. Diese Regeln begrenzen die Folgen fehlerhafter
Zugriffe und unterstützen eine datensparsame Umsetzung.

### 3. Domain Integrity and Traceable History
Fachliche Invarianten MÜSSEN an der serverseitigen Daten- beziehungsweise
Transaktionsgrenze durchgesetzt werden, auch bei parallelen Änderungen. Dazu gehören
die Obergrenze von drei aktiven Mannschaftsverantwortlichen, höchstens eine aktive
Mannschaft pro Spieler, eine Waschfamilie pro Spiel und die Kapazitätsgrenze je
Bewirtungsposten. Spielerwechsel MÜSSEN Identität und Mannschaftshistorie erhalten;
Sammelwechsel MÜSSEN nach Vorschau und Bestätigung vollständig oder gar nicht
ausgeführt werden. Aufgabenübernahmen bleiben ihrer Mannschaft und ihrem Spiel
zugeordnet und werden nicht mit einem Spielerwechsel übertragen.

### 4. Mobile-First, Role-Aware Usability
Das Elternportal MUSS auf Smartphones ohne horizontales Scrollen für zentrale
Aufgaben nutzbar sein. Verwaltungsabläufe MÜSSEN auf Tablet und Desktop bedienbar
sein. Jede rollenübergreifende oder mannschaftsübergreifende Ansicht MUSS den
aktuellen Arbeitsbereich und die aktive Mannschaft eindeutig kenntlich machen.
Aufgabenstatus, freie Kapazitäten und erfolgreiche Eintragungen MÜSSEN klar
erkennbar sein. Dadurch werden Verwechslungen zwischen Familien, Rollen und
Mannschaften vermieden.

### 5. Verifiable, Focused Changes
Jede Änderung MUSS durch Tests oder eine dokumentierte, reproduzierbare manuelle
Prüfung belegen, dass die betroffenen Anforderungen und Berechtigungsgrenzen
eingehalten werden. Änderungen an fachlichen Invarianten MÜSSEN insbesondere
Grenzfälle und konkurrierende Schreibvorgänge berücksichtigen. Implementierungen
MÜSSEN auf den notwendigen Umfang beschränkt bleiben und dürfen bestehende
Aufgaben-, Saison- oder Spielerhistorie nicht stillschweigend entfernen oder
überschreiben.

## Additional Constraints

- Die bestehende Frontend-Basis ist React, TypeScript und Vite. Abweichungen,
  zusätzliche Laufzeitdienste oder Abhängigkeiten MÜSSEN durch einen konkreten
  Bedarf begründet und im Projektentscheid festgehalten werden.
- Importabläufe MÜSSEN vor der Übernahme eine Vorschau und erkennbare
  Validierungsergebnisse bieten. Fehlerhafte Zeilen DÜRFEN gültige Zeilen nicht
  blockieren; ein erneuter Import DARF bestehende Aufgabenübernahmen nicht
  unbemerkt löschen oder überschreiben.
- Deaktivierte Zuordnungen und abgeschlossene Saison- beziehungsweise
  Mannschaftszuordnungen MÜSSEN für erforderliche Nachvollziehbarkeit erhalten
  bleiben. Änderungen dürfen historische Datensätze nicht als aktuelle
  Zuordnungen ausweisen.
- Sicherheits- und Datenintegritätsregeln DÜRFEN nicht ausschließlich in der
  Benutzeroberfläche umgesetzt werden.
- Die lokale Containerumgebung MUSS Podman Desktop unterstützen und DARF keine
  Installation von Docker Desktop oder Docker Engine voraussetzen. Dies gilt
  auch für lokale Dienste, Compose-Abläufe und containerbasierte Tests.

## Development Workflow and Quality Gates

- Spezifikationen, Pläne und Implementierungen MÜSSEN auf die betroffenen
  Anforderungen und beobachtbaren Ergebnisse zurückführbar sein. Nicht
  entschiedene Produktfragen DÜRFEN nicht stillschweigend als verbindliche
  Fachregeln umgesetzt werden.
- Für geändertes Verhalten MÜSSEN passende Tests ergänzt oder angepasst werden.
  Zugriffsregeln, Teamgrenzen und fachliche Invarianten benötigen Prüfungen an
  der serverseitigen Grenze, sobald diese verfügbar ist.
- Vor einer Freigabe MÜSSEN die relevanten Tests sowie `npm run lint` und
  `npm run build` erfolgreich sein. Diese statischen Prüfungen ersetzen keine
  Verhaltens- oder Berechtigungsprüfung.
- Die Einrichtung und Ausführung containerbasierter Tests MÜSSEN für Podman
  Desktop dokumentiert und unter dieser Umgebung überprüft werden. Fehlende
  Container-Laufzeiten oder nicht ausgeführte Tests MÜSSEN als offene
  Validierung ausgewiesen werden und gelten nicht als bestandene Tests.
- Reviews MÜSSEN Änderungen an Datenschutz, Rollen, Teamgrenzen, Historie und
  Datenimport ausdrücklich gegen diese Verfassung prüfen. Abweichungen benötigen
  eine dokumentierte Begründung und Genehmigung vor der Freigabe.

## Governance

Diese Verfassung ist für Spezifikationen, Pläne, Implementierungen und Reviews
verbindlich. Projektdokumente dürfen ihre Regeln konkretisieren, aber ihnen nicht
widersprechen. Ein Konflikt muss vor der Umsetzung durch Klärung der Spezifikation
oder eine genehmigte Verfassungsänderung behoben werden.

Änderungen an dieser Verfassung müssen mit Begründung und Auswirkungsbericht
vorgeschlagen und vom Projektverantwortlichen geprüft werden. Der Versionsstand
folgt Semantic Versioning: MAJOR für rückwärtsinkompatible Änderungen an Regeln,
MINOR für neue oder wesentlich erweiterte Prinzipien und Abschnitte, PATCH für
Klarstellungen ohne Bedeutungsänderung. Jede angenommene Änderung aktualisiert
Versionsnummer, Änderungsdatum und Sync Impact Report.

Bei Spezifikation, Planung, Review und Freigabe muss die Konformität geprüft
werden. Nicht erfüllte Regeln oder genehmigte Ausnahmen müssen mit Umfang,
Begründung, Genehmigung und gegebenenfalls einem Ablaufdatum dokumentiert sein.

**Version**: 1.1.0 | **Ratified**: TODO(RATIFICATION_DATE): ursprüngliches Annahmedatum nicht dokumentiert | **Last Amended**: 2026-11-19
