# Feature Specification: Vereinsweites Mannschaftsmanagement

**Feature Branch**: `Not created`

**Created**: 2026-11-19

**Status**: Draft

**Input**: User description: Use [SRS_Mannschaftsmanagement.md](../../SRS_Mannschaftsmanagement.md) (Version 2.2) as the product requirements source.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Verein und Mannschaften verwalten (Priority: P1)

Ein Vereinsadministrator richtet den Verein und seine Mannschaften ein, verwaltet deren
Status und bestimmt die Mannschaftsverantwortlichen. So kann der Verein den Betrieb
mehrerer Jugendmannschaften zentral organisieren.

**Why this priority**: Mannschaften und verantwortliche Personen bilden die Grundlage
für alle weiteren Funktionen und Berechtigungen.

**Independent Test**: Mit einem Vereinsadministrator eine Mannschaft anlegen, bearbeiten
und deaktivieren; drei Mannschaftsverantwortliche ernennen; anschließend prüfen, dass
eine vierte Ernennung abgewiesen wird und ein Benutzer ohne Vereinsadministratorrecht
keine Mannschaft anlegen kann.

**Acceptance Scenarios**:

1. **Given** ein Vereinsadministrator und ein Verein, **When** er eine Mannschaft mit
   Name und Saison anlegt, **Then** erscheint die Mannschaft in der Vereinsübersicht
   mit eindeutigem Status.
2. **Given** eine Mannschaft mit drei aktiven Mannschaftsverantwortlichen, **When**
   ein weiterer ernannt werden soll, **Then** wird die Ernennung abgewiesen und die
   bestehenden Ernennungen bleiben unverändert.
3. **Given** ein Benutzer ohne Vereinsadministratorrecht, **When** er eine
   vereinsweite Verwaltungsaktion versucht, **Then** wird die Aktion verweigert und
   es werden keine geschützten Daten offengelegt.
4. **Given** ein Vereinsadministrator ernennt oder entlässt eine
   Mannschaftsverantwortung, **When** die Änderung wirksam wird, **Then** werden
   ausführende Person und Zeitpunkt nachvollziehbar gespeichert.

### User Story 2 - Spieler, Familien und Einladungen verwalten (Priority: P1)

Ein Mannschaftsverantwortlicher verwaltet Spieler und Familien seiner Mannschaft,
verknüpft mehrere Spieler mit einer Familie und lädt Elternteile zur Mitarbeit ein.
Vereinsadministratoren können Spieler vereinsweit verwalten und Mannschaftszuordnungen
pflegen.

**Why this priority**: Familien und Spieler bilden die organisatorische Grundlage für
Elternzugänge und Aufgabenübernahmen.

**Independent Test**: Eine Familie mit mehreren Spielern anlegen, zwei Elternteile
einladen und beide Einladungen annehmen lassen; prüfen, dass beide dieselben
Familienaufgaben sehen, aber keinen Zugriff auf eine nicht zugeordnete Mannschaft
erhalten.

**Acceptance Scenarios**:

1. **Given** ein Mannschaftsverantwortlicher, **When** er eine Familie und mehrere
   Spieler erfasst, **Then** bleiben die Spielerdatensätze eigenständig und sind der
   Familie sowie der berechtigten Mannschaft zugeordnet.
2. **Given** eine Einladung zu einer Familie und Mannschaft, **When** ein Elternteil
   eine gültige Einladung annimmt, **Then** erhält es Zugriff nur auf die
   zugeordneten Familien- und Mannschaftsdaten.
3. **Given** eine abgelaufene, widerrufene oder bereits angenommene Einladung,
   **When** sie erneut verwendet wird, **Then** entsteht kein Zugriff.
4. **Given** eine Familie mit zwei zugehörigen Elternkonten, **When** eines davon
   eine Familienaufgabe übernimmt, **Then** sehen beide Konten dieselbe
   Familienaufgabe.
5. **Given** eine aktive Familienzuordnung mit bestehenden Aufgaben, **When** der
   Mannschaftsverantwortliche sie deaktiviert, **Then** kann die Familie keine
   neuen Aufgaben übernehmen und bisherige Eintragungen bleiben nachvollziehbar.
6. **Given** eine Datei mit Familien- oder Spielerdaten, **When** der
   Mannschaftsverantwortliche den Import prüft, **Then** sieht er vor der
   Übernahme eine Vorschau und einzeln erkennbare fehlerhafte Datensätze.

### User Story 3 - Spielplan pflegen und importieren (Priority: P1)

Ein Mannschaftsverantwortlicher pflegt Heim- und Auswärtsspiele, sieht vergangene und
kommende Termine und importiert mehrere Spiele aus einer Datei. Dadurch können
Familien ihre Aufgaben für die Mannschaft verlässlich planen.

**Why this priority**: Spiele sind der gemeinsame Bezugspunkt für Trikotwäsche,
Bewirtung und die Familienübersicht.

**Independent Test**: Spiele manuell erfassen und eine Datei mit gültigen, fehlerhaften
und möglichen doppelten Zeilen importieren; prüfen, dass eine Vorschau erscheint,
gültige Zeilen übernommen, Fehler ausgewiesen und vorhandene Aufgaben nicht
überschrieben werden.

**Acceptance Scenarios**:

1. **Given** ein Mannschaftsverantwortlicher, **When** er ein Heim- oder
   Auswärtsspiel erfasst oder bearbeitet, **Then** werden Datum, Uhrzeit, Gegner,
   Spieltyp und optionale Orts- oder Bemerkungsangaben angezeigt.
2. **Given** eine Datei mit gültigen und ungültigen Spielzeilen, **When** der
   Verantwortliche den Import prüft und bestätigt, **Then** werden gültige Zeilen
   übernommen und ungültige Zeilen mit nachvollziehbarem Fehler ausgewiesen.
3. **Given** ein erneuter Import mit möglichen Duplikaten, **When** eine vorhandene
   Partie bereits Aufgabenübernahmen besitzt, **Then** werden Duplikat und Folgen
   vor der Übernahme kenntlich gemacht; bestehende Übernahmen werden nicht
   stillschweigend entfernt oder geändert.
4. **Given** ein vergangenes Spiel und ein noch bevorstehendes Spiel, **When**
   der Verantwortliche den Spielplan öffnet, **Then** bleiben vergangene Spiele
   einsehbar und sind von kommenden Spielen unterscheidbar.
5. **Given** ein bevorstehendes Spiel, **When** der Verantwortliche es aus dem
   aktiven Spielplan entfernt, **Then** muss er die Aktion bestätigen und
   vorhandene Aufgabenhistorie bleibt nachvollziehbar.

### User Story 4 - Familienaufgaben übernehmen (Priority: P1)

Ein berechtigtes Elternteil sieht kommende Spiele und freie beziehungsweise belegte
Trikotwäsche- und Bewirtungsaufgaben und trägt die eigene Familie mit möglichst
wenigen Schritten ein. Elternteile können die eigenen Eintragungen zurücknehmen und
ihre Familienaufgaben über mehrere Mannschaften hinweg einsehen.

**Why this priority**: Die selbstständige Aufgabenübernahme ist der zentrale
Elternnutzen der Anwendung und reduziert die organisatorische Koordination.

**Independent Test**: Auf einem Smartphone als Elternteil eine freie Wasch- und
Bewirtungsaufgabe der eigenen Familie übernehmen, den aktualisierten Status prüfen,
eine Eintragung zurücknehmen und die teamübergreifende Aufgabenübersicht aufrufen.

**Acceptance Scenarios**:

1. **Given** eine Familie mit gültiger Zuordnung zu einer Mannschaft und eine freie
   Aufgabe, **When** ein zugehöriger Elternteil die Familie einträgt, **Then**
   erscheint die Belegung für diese Familie und die Aufgabe wird aktualisiert.
2. **Given** eine Familie ohne Zuordnung zur betreffenden Mannschaft, **When** ein
   Elternteil eine Aufgabe übernehmen will, **Then** wird die Eintragung verweigert.
3. **Given** mehrere gleichzeitige Eintragungsversuche für den letzten verfügbaren
   Platz, **When** die Aktionen verarbeitet werden, **Then** wird die Kapazität
   eingehalten und höchstens eine Familie erhält den Platz.
4. **Given** eine Familie mit Aufgaben in mehreren Mannschaften, **When** ein
   zugehöriges Elternteil „Meine Aufgaben“ öffnet, **Then** sind jede Aufgabe und
   ihre Mannschaft eindeutig erkennbar.
5. **Given** ein Elternteil öffnet das Dashboard, **When** kommende Spiele oder
   eigene Aufgaben offen sind, **Then** sieht es das nächste Spiel und die
   zugehörigen offenen Aufgaben sowie eine Bestätigung nach erfolgreicher
   Eintragung.

### User Story 5 - Wasch- und Bewirtungsaufgaben organisieren (Priority: P1)

Eine Elternkoordination oder ein Mannschaftsverantwortlicher organisiert
Waschaufgaben und Bewirtungslisten, korrigiert Eintragungen und sieht, welche
Aufgaben noch offen sind. Die Elternkoordination kann keine Mannschaften,
Spielerzuordnungen oder Mannschaftsverantwortlichen verwalten.

**Why this priority**: Die Koordination muss offene Aufgaben zuverlässig verteilen
und Familien bei Fehlern unterstützen können.

**Independent Test**: Eine Elternkoordination erstellt Bewirtungsposten für ein
Heimspiel, setzt benötigte Kapazitäten, korrigiert eine Zuordnung und kopiert die
Liste auf ein anderes Heimspiel; prüfen, dass die Kopie leer startet und unabhängig
vom Original geändert werden kann.

**Acceptance Scenarios**:

1. **Given** ein Heimspiel, **When** die Elternkoordination Bewirtung aktiviert und
   Posten mit benötigten Mengen erstellt, **Then** werden freie und belegte Plätze
   sowie der Gesamtstatus erkennbar.
2. **Given** ein Auswärtsspiel, **When** eine Person die Spiel- oder Aufgabenansicht
   öffnet, **Then** wird keine Bewirtungsliste für dieses Spiel angeboten.
3. **Given** ein vorhandener Bewirtungsposten und eine belegte Waschaufgabe,
   **When** eine berechtigte Koordination eine Zuordnung korrigiert, **Then** wird
   der Status aktualisiert und die Änderung bleibt auf ihre Mannschaft beschränkt.
4. **Given** eine Bewirtungsliste eines Heimspiels, **When** sie auf ein anderes
   Heimspiel kopiert wird, **Then** entstehen unabhängige, unbelegte Posten; spätere
   Änderungen an einer Liste verändern die andere nicht.
5. **Given** eine Mannschaft ohne aktive Elternkoordination, **When** ein
   Mannschaftsverantwortlicher eine berechtigte Person ernennt, **Then** ist genau
   diese Person aktiv und eine weitere gleichzeitige Ernennung wird abgewiesen.
6. **Given** eine Waschaufgabe, **When** die eingetragene Familie oder eine
   berechtigte Koordination sie als erledigt markiert, **Then** wird der Status
   „Erledigt“ angezeigt.

### User Story 6 - Mehrere Rollen und Mannschaften nutzen (Priority: P1)

Ein Benutzer nutzt ein einziges Konto für mehrere Mannschaften und kann innerhalb
einer Mannschaft mehr als eine Rolle besitzen. Er erkennt jederzeit die aktive
Mannschaft und wechselt zwischen Familien- und Verwaltungsansichten, ohne sich
erneut anzumelden.

**Why this priority**: Familien können Kinder in unterschiedlichen Mannschaften
haben; außerdem können Trainer selbst Eltern sein. Getrennte Konten oder unklare
Kontexte würden zu Fehlern und unnötigem Aufwand führen.

**Independent Test**: Ein Konto erhält Eltern- und Verwaltungsrollen in einer
Mannschaft sowie eine Elternzuordnung in einer zweiten Mannschaft; ohne erneute
Anmeldung zwischen Mannschaften und Ansichten wechseln und jeweils nur die
zulässigen Funktionen prüfen.

**Acceptance Scenarios**:

1. **Given** ein Benutzer mit mehreren Mannschaftszuordnungen, **When** er die
   aktive Mannschaft wechselt, **Then** werden Daten und Berechtigungen dem neuen
   Mannschaftskontext entsprechend angezeigt.
2. **Given** ein Mannschaftsverantwortlicher, der einer Familie angehört, **When**
   er die Elternansicht öffnet, **Then** kann er Aufgaben nur für seine tatsächlich
   zugeordnete Familie übernehmen.
3. **Given** eine Rolle wird ernannt oder abberufen, **When** die Änderung wirksam
   wird, **Then** bleiben Familienzugehörigkeiten und bestehende Aufgabenübernahmen
   unverändert.

### User Story 7 - Saisonwechsel nachvollziehbar durchführen (Priority: P2)

Ein Vereinsadministrator überprüft den Kader einer Quellmannschaft, wählt Spieler
für eine Zielmannschaft der neuen Saison aus und bestätigt den Wechsel. Die
Spieleridentität und Mannschaftshistorie bleiben erhalten; Rollen und
Familienaufgaben werden nicht automatisch übertragen.

**Why this priority**: Der Saisonwechsel erleichtert die jährliche Kaderplanung,
muss aber vor allem die Spielerhistorie und Zugriffsgrenzen schützen.

**Independent Test**: Eine Vorschau für mehrere Spieler erstellen, die Änderung
abbrechen und prüfen, dass keine Daten geändert wurden; anschließend erneut
bestätigen und prüfen, dass alle ausgewählten Spieler oder keiner versetzt wird,
frühere Zuordnungen erhalten bleiben und keine Aufgaben kopiert werden.

**Acceptance Scenarios**:

1. **Given** mehrere ausgewählte Spieler und eine Zielmannschaft, **When** der
   Administrator die Saisonwechselvorschau öffnet, **Then** werden Spieler,
   Quell- und Zielmannschaft sowie Saisons vor der Bestätigung angezeigt.
2. **Given** eine Vorschau, **When** der Administrator abbricht, **Then** bleiben
   alle bisherigen Mannschaftszuordnungen unverändert.
3. **Given** gültige Zielzuordnungen, **When** der Administrator den Wechsel
   bestätigt, **Then** werden alle gewählten Spieler versetzt oder bei einem
   Fehler keine; frühere Zuordnungen bleiben in der Historie.
4. **Given** ein bestätigter Spielerwechsel, **When** die Historie und Aufgaben der
   Familie angezeigt werden, **Then** bleiben frühere Mannschaftszuordnungen
   sichtbar, aber Wasch- und Bewirtungsaufgaben werden nicht übertragen.
5. **Given** eine abgeschlossene Saison, **When** ein Vereinsadministrator deren
   Spiele und Aufgabendaten archiviert, **Then** bleiben sie für die
   Nachvollziehbarkeit einsehbar und werden nicht als aktuelle Aufgaben angezeigt.

### Edge Cases

- Ein Benutzer besitzt mehrere Rollen, aber keine Familienzuordnung; er darf
  dadurch keine Familienaufgabe übernehmen.
- Eine vierte Mannschaftsverantwortung oder eine zweite aktive Elternkoordination
  wird vorgeschlagen; bestehende Ernennungen bleiben bei Ablehnung unverändert.
- Zwei gleichzeitige Anmeldungen konkurrieren um die letzte Wasch- oder
  Bewirtungskapazität; die Aufgabe darf nicht überbelegt werden.
- Eine Familie ist deaktiviert, nicht mehr der Mannschaft zugeordnet oder mit
  mehreren Mannschaften verbunden; neue Übernahmen sind nur in aktiven,
  berechtigten Mannschaftskontexten zulässig.
- Ein Spieler hat bereits eine aktive Mannschaftszuordnung oder ein Saisonwechsel
  enthält ungültige Zielzuordnungen; es darf keine doppelte aktive Zuordnung geben
  und ein Sammelwechsel darf nicht teilweise ausgeführt werden.
- Eine Einladung ist abgelaufen, widerrufen oder bereits angenommen; sie darf
  keinen neuen Zugang gewähren.
- Ein Import enthält ungültige Pflichtfelder, Duplikate oder vorhandene
  Aufgabenübernahmen; Fehler müssen sichtbar sein und gültige Zeilen dürfen nicht
  stillschweigend bestehende Übernahmen überschreiben.
- Eine Elternkoordination versucht, Spieler- oder Vereinsdaten zu ändern; die
  Aktion wird verweigert.
- Ein Benutzer versucht über einen fremden Mannschaftskontext auf Familien-,
  Spieler- oder Aufgabeninformationen zuzugreifen; es werden keine geschützten
  Daten offengelegt.
- Ein Spiel wird nach seiner Anlage geändert oder aus dem aktiven Spielplan
  entfernt; zugehörige Aufgaben und historische Nachvollziehbarkeit dürfen nicht
  unbemerkt verfälscht werden.

## Clarifications

### Session 2026-11-19

- Each parent needs explicit access to each team; family membership alone never grants access to another team.
- A team manager must explicitly reactivate an inactive family-team membership before an invitation can be accepted.
- Club administrators may view and manage families and memberships across the club; changes are audited.
- The club is set up once; administrators may subsequently edit its name, unique key, and time zone.
- A parent coordinator must be an active parent linked to a family in that team.
- Archived seasons expose matches and duties as read-only history, including incomplete duties; no assignment corrections are permitted.

### Additional Acceptance Checks

- **US1**: After one-time club setup, an administrator can edit name, key, and
  time zone; a non-administrator cannot edit the club profile.
- **US2/US6**: A family belongs to Teams A and B. A parent invited only to A
  cannot list B as a parent context or read B's schedules or family tasks.
- **US2**: Acceptance against an inactive family-team membership fails without
  consuming the invitation. After explicit manager reactivation, an otherwise
  valid invitation can be accepted. Club-wide family administration and
  membership changes are authorized and audited.
- **US5**: A manager-only user and a parent of another team cannot be appointed
  coordinator. Losing active parent eligibility removes effective coordinator
  authority in that team.
- **US7**: Archiving a season removes even incomplete duties from current lists,
  preserves authorized historical reads, and rejects match/duty changes for
  parents, coordinators, managers, and administrators.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Das System MUSS einen Verein mit Name und eindeutiger Kennung sowie
  mehrere zugehörige Mannschaften verwalten können.
  Nach einmaliger Einrichtung MÜSSEN Vereinsadministratoren Vereinsname,
  eindeutige Kennung und Zeitzone bearbeiten können.
- **FR-002**: Ein Vereinsadministrator MUSS Mannschaften mit Name, Saison,
  optionaler Beschreibung und aktivem oder inaktivem Status anlegen, bearbeiten
  und deaktivieren können.
- **FR-003**: Nur ein Vereinsadministrator DARF Mannschaftsverantwortliche
  ernennen oder abberufen; je Mannschaft DÜRFEN höchstens drei aktive
  Mannschaftsverantwortliche bestehen.
- **FR-004**: Ein Mannschaftsverantwortlicher MUSS eine aktive Elternkoordination
  ernennen oder abberufen können; je Mannschaft DARF höchstens eine aktiv sein.
  Die ernannte Person MUSS ein aktives Elternteil einer aktiv zugeordneten Familie
  dieser Mannschaft mit expliziter Zugriffsberechtigung auf diese Mannschaft sein.
- **FR-005**: Ein Benutzer MUSS mehrere Rollen innerhalb derselben Mannschaft und
  unterschiedliche Rollen in mehreren Mannschaften besitzen können.
- **FR-006**: Mannschaftsverantwortliche MÜSSEN Familien und deren Zuordnungen
  innerhalb ihrer Mannschaft verwalten sowie Familien deaktivieren können. Eine
  Familie MUSS einen Familiennamen haben können; Ansprechpartner und
  Kontakt-E-Mail sind optional. Deaktivierte Zuordnungen DÜRFEN keine neuen
  Aufgaben übernehmen; vorhandene Eintragungen bleiben nachvollziehbar.
  Vereinsadministratoren DÜRFEN Familien und Mitgliedschaften vereinsweit
  verwalten. Eine deaktivierte Familien-Mannschaftszuordnung MUSS vor Annahme
  einer Einladung ausdrücklich durch einen Mannschaftsverantwortlichen reaktiviert
  werden; Einladungen DÜRFEN diese Reaktivierung nicht selbst auslösen.
- **FR-007**: Eine Familie MUSS mehrere Spieler und mehrere Elternkonten haben
  können. Familien- und Rollenmitgliedschaften MÜSSEN getrennt behandelt werden;
  eine Familie DARF mehreren Mannschaften zugeordnet sein; alle zugehörigen
  Elternkonten sehen dieselben Familienaufgaben innerhalb der Mannschaften, für
  die das jeweilige Elternteil ausdrücklich zugriffsberechtigt ist. Eine
  Familienzugehörigkeit DARF keinen automatischen Zugriff auf weitere
  Mannschaften der Familie gewähren.
- **FR-008**: Benutzer MÜSSEN sich anmelden und dasselbe Konto für mehrere
  Mannschaften verwenden können. Mannschaftsverantwortliche MÜSSEN Eltern über
  eine E-Mail-Adresse zu einer Familie und Mannschaft einladen können. Einladungen
  MÜSSEN einen sichtbaren Status, Widerruf, Ablauf und einmalige Annahme
  unterstützen.
- **FR-009**: Spieler MÜSSEN vereinsweit mit stabiler Identität verwaltet und
  Familien zugeordnet werden können und mindestens einen Namen haben.
  Mannschaftsverantwortliche verwalten Spieler ihrer Mannschaft;
  Vereinsadministratoren können Spieler vereinsweit anlegen, bearbeiten und
  zuordnen.
- **FR-010**: Pro Spieler DARF zu jedem Zeitpunkt höchstens eine aktive
  Mannschaftszuordnung bestehen. Eine Zuordnung MUSS Mannschaft und Saison
  enthalten; abgeschlossene Zuordnungen dürfen nicht durch einen Wechsel
  überschrieben werden.
- **FR-011**: Berechtigte Vereinsadministratoren MÜSSEN die aktuelle und
  chronologische frühere Mannschaftszuordnung eines Spielers einsehen können.
- **FR-012**: Mannschaftsverantwortliche MÜSSEN Spiele ihrer Mannschaft mit Datum,
  Uhrzeit, Gegner, Heim-/Auswärtsstatus und optionalem Spielort oder Bemerkung
  anlegen, bearbeiten und nach Bestätigung aus dem aktiven Spielplan entfernen
  können.
- **FR-013**: Jedes Spiel MUSS genau einer Mannschaft zugeordnet sein; Nutzer
  DÜRFEN nur Spiele ihrer berechtigten Mannschaften verwalten.
- **FR-014**: Die Spielliste MUSS kommende und vergangene Spiele chronologisch
  auffindbar machen und vergangene Spiele von kommenden unterscheiden.
- **FR-015**: Mannschaftsverantwortliche MÜSSEN Spiele aus CSV-Dateien importieren
  können. Vor der Übernahme MUSS das System Vorschau, Feldzuordnung,
  Validierungsergebnisse und mögliche Duplikate anzeigen. Gültige Zeilen können
  trotz fehlerhafter Zeilen übernommen werden; vorhandene Wasch- und
  Bewirtungszuordnungen dürfen nicht stillschweigend gelöscht oder überschrieben
  werden.
- **FR-016**: Für jedes Spiel MUSS eine Trikotwaschaufgabe vorgesehen sein. Je
  Spiel DARF höchstens eine berechtigte Familie eingetragen sein.
- **FR-017**: Waschaufgaben MÜSSEN mindestens den Status frei, vergeben oder
  erledigt unterstützen. Eintragungen und Kapazitätsgrenzen MÜSSEN auch bei
  gleichzeitigen Aktionen eingehalten werden.
- **FR-018**: Ein Elternteil MUSS die eigene Familie für eine berechtigte
  Waschaufgabe ein- und austragen können. Die Familie, ein Mannschaftsverantwortlicher
  oder die Elternkoordination MUSS eine Waschaufgabe als erledigt markieren können.
- **FR-019**: Mannschaftsverantwortliche und Elternkoordination MÜSSEN
  Waschzuordnungen verwalten und korrigieren können; sie DÜRFEN dies nur innerhalb
  ihrer Mannschaft tun.
- **FR-020**: Bewirtung MUSS ausschließlich für Heimspiele angeboten werden.
  Mannschaftsverantwortliche und Elternkoordination MÜSSEN die Bewirtung aktivieren
  und beliebig viele Posten mit benötigter Anzahl erstellen können.
- **FR-021**: Ein Bewirtungsposten MUSS mehrere Familien entsprechend seiner
  benötigten Anzahl aufnehmen können. Die Kapazität DARF auch bei gleichzeitigen
  Eintragungen nicht überschritten werden.
- **FR-022**: Eine Familie MUSS einen Bewirtungsposten höchstens einmal belegen
  können und die eigene Eintragung entfernen können. Mannschaftsverantwortliche
  und Elternkoordination MÜSSEN Eintragungen korrigieren oder entfernen können.
- **FR-023**: Eine Bewirtungsliste MUSS auf ein anderes Heimspiel kopiert werden
  können. Kopierte Posten MÜSSEN unabhängige Datensätze sein und ohne übernommene
  Familienbelegungen starten.
- **FR-024**: Das Dashboard MUSS das nächste Spiel, Heim-/Auswärtsstatus und
  offene Wasch- und Bewirtungsaufgaben anzeigen; Elternteile sehen zusätzlich die
  nächsten Aufgaben ihrer Familie.
- **FR-025**: Familien MÜSSEN eine Übersicht ihrer kommenden und vergangenen
  Wasch- und Bewirtungsaufgaben mit Spiel, Datum und Mannschaft erhalten können.
- **FR-026**: Das System MUSS getrennte und verständliche Arbeitsbereiche für
  Vereinsadministration, Mannschaftsverwaltung und Elternportal anbieten.
- **FR-027**: Ein Benutzer mit mehreren Mannschaftszuordnungen MUSS die aktive
  Mannschaft ohne erneute Anmeldung wechseln können. Aktive Mannschaft und
  Arbeitsbereich MÜSSEN jederzeit eindeutig erkennbar sein.
- **FR-028**: Jede Lese- und Änderungsaktion auf personenbezogene oder
  mannschaftsbezogene Daten MUSS die Berechtigung für die betroffene Mannschaft
  und Rolle prüfen. Nicht angemeldete oder nicht berechtigte Nutzer DÜRFEN keine
  Familien- oder Spielerdaten erhalten; eine übermittelte Mannschaftskennung
  allein DARF keinen Zugriff gewähren.
- **FR-029**: Das System MUSS nur die für die Vereinsorganisation notwendigen
  personenbezogenen Daten verwalten. Ernennungen und Abberufungen von Rollen
  MÜSSEN mit ausführender Person und Zeitpunkt nachvollziehbar sein.
  Administrative Änderungen an Familien-, Elternzugriffs- und
  Spielerzuordnungen MÜSSEN ebenfalls mit ausführender Person, Zeitpunkt und
  betroffener Zuordnung nachvollziehbar protokolliert werden.
- **FR-030**: Vereinsadministratoren MÜSSEN Saisons verwalten und vergangene
  Spiel- und Aufgabendaten archiviert einsehen können. Familien- und
  Spielerzuordnungen können für eine Folgesaison übernommen werden; Rollen und
  Einladungsstatus müssen dabei erneut geprüft werden.
  Archivierte Spiele und Aufgaben DÜRFEN nur schreibgeschützt in der Historie
  erscheinen, nicht als aktuelle Aufgaben; dies gilt auch für unerledigte Aufgaben.
  Eintragungen und Korrekturen in archivierten Saisons sind für alle Rollen gesperrt.
- **FR-031**: Ein Vereinsadministrator MUSS für einen Saisonwechsel einzelne oder
  mehrere Spieler mit Vorschau von Quell- und Zielmannschaft sowie Saison versetzen
  können. Erst nach ausdrücklicher Bestätigung wird der Wechsel ausgeführt; ein
  Sammelwechsel erfolgt vollständig oder gar nicht.
- **FR-032**: Ein Spielerwechsel DARF weder einen doppelten Spielerdatensatz noch
  eine automatische Übertragung von Rollen, Waschaufgaben oder
  Bewirtungsübernahmen erzeugen. Elternzugriff auf die Zielmannschaft MUSS separat
  geprüft und bei Bedarf durch eine Einladung vorbereitet werden.
- **FR-033**: Mannschaftsverantwortliche MÜSSEN Spiele, Bewirtungsposten,
  Familien und Spieler aus CSV- oder JSON-Dateien importieren können. Vor der
  Übernahme MUSS eine Vorschau mit erkennbaren fehlerhaften Datensätzen und
  Validierung erscheinen; ungültige Datensätze dürfen gültige nicht verhindern.
- **FR-034**: Eltern MÜSSEN zentrale Aufgaben auf Smartphones bedienen können;
  Verwaltungsoberflächen MÜSSEN auf Tablet und Desktop bedienbar sein. Freie,
  teilweise belegte und vollständig belegte Aufgaben sowie erfolgreiche
  Eintragungen MÜSSEN klar unterscheidbar sein.

### Key Entities *(include if feature involves data)*

- **Verein**: Organisatorischer Rahmen mit Name und eindeutiger Kennung.
- **Mannschaft**: Mannschaft eines Vereins mit Name, Saison, Beschreibung, Status
  und Mannschaftsverantwortlichen.
- **Saison**: Zeitraum, dem Mannschaften und Spielerzuordnungen zugeordnet sind.
- **Benutzer und Mannschaftsrolle**: Benutzerkonto mit einer oder mehreren
  kontextbezogenen Rollen; Rollen sind unabhängig von Familienmitgliedschaften.
- **Familie**: Trägerin von Aufgabenübernahmen, mit einem oder mehreren Spielern,
  Elternkonten und Mannschaftszuordnungen.
- **Spieler und Mannschaftszuordnung**: Vereinsweit identifizierter Spieler mit
  Familienbezug sowie aktuellen und abgeschlossenen Mannschafts- und
  Saisonzuordnungen.
- **Einladung**: Zeitlich begrenzte, widerrufbare Zuordnung eines Elternteils zu
  einer Familie und Mannschaft.
- **Spiel**: Mannschaftsbezogener Termin mit Datum, Uhrzeit, Gegner, Ort und
  Heim-/Auswärtsstatus.
- **Waschaufgabe und Bewirtungsposten**: Spielbezogene Aufgaben mit Status,
  erforderlicher Kapazität und Familienzuordnungen.
- **Aufgabenübernahme**: Zuordnung einer Familie zu einer Waschaufgabe oder einem
  Bewirtungsplatz, getrennt von der Spielerhistorie.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In allen definierten Rollen- und Mannschaftskontexten können
  unberechtigte Nutzer weder geschützte Familien- oder Spielerdaten einsehen noch
  Änderungen an fremden Mannschaftsdaten vornehmen.
- **SC-002**: 100 % der Prüfungen für drei aktive Mannschaftsverantwortliche,
  eine aktive Elternkoordination, eine aktive Mannschaft pro Spieler, eine
  Waschfamilie pro Spiel und Bewirtungskapazität halten die jeweiligen Grenzen
  auch bei gleichzeitigen Aktionen ein.
- **SC-003**: Ein Benutzer mit mehreren Mannschaftsrollen kann in allen
  vorgesehenen Rollen und Mannschaften mit einem Konto arbeiten, ohne sich beim
  Mannschaftswechsel erneut anzumelden; der aktive Kontext ist in jedem getesteten
  Arbeitsbereich erkennbar.
- **SC-004**: Mindestens 9 von 10 Elternteilen in einem repräsentativen
  Nutzungstest können auf einem Smartphone eine freie Familienaufgabe innerhalb
  von 60 Sekunden ohne Unterstützung übernehmen.
- **SC-005**: Bei jedem geprüften Import werden alle gültigen Zeilen unabhängig
  von fehlerhaften Zeilen korrekt ausgewiesen; kein geprüfter Wiederholungsimport
  löscht oder ändert stillschweigend bestehende Familienaufgaben.
- **SC-006**: Bei jedem geprüften Saisonwechsel bleiben Spieleridentität und
  abgeschlossene Zuordnungen erhalten; Abbruch verändert keine Zuordnung und
  bestätigte Sammelwechsel werden vollständig oder gar nicht ausgeführt.
- **SC-007**: Mindestens 9 von 10 Testpersonen können im Elternportal die aktive
  Mannschaft und den Belegungsstatus einer ausgewählten Aufgabe innerhalb von
  10 Sekunden korrekt erkennen.

## Assumptions

- Die erste Produktfassung dient einem Verein mit mehreren Mannschaften; eine
  Verwaltung mehrerer voneinander unabhängiger Vereine ist nicht Teil dieses
  Umfangs.
- Mannschaftsverantwortliche werden ausschließlich durch Vereinsadministratoren
  ernannt. Pro Mannschaft sind höchstens drei aktiv; pro Mannschaft ist höchstens
  eine Elternkoordination gleichzeitig aktiv.
- Ein Benutzer kann mehrere Mannschafts- und Familienzuordnungen haben. Ein
  Rollenwechsel verändert keine Familienzugehörigkeit oder bestehende
  Aufgabenübernahme.
- Mannschaften werden im fachlichen Kontext je Saison geführt. Ein Spieler kann
  abgeschlossene historische Zuordnungen haben, aber höchstens eine aktive
  Mannschaftszuordnung.
- Ein Spielerwechsel gewährt Eltern nicht automatisch Zugriff auf eine andere
  Mannschaft. Die Zielzuordnung und erforderliche Einladung werden gesondert
  geprüft.
- Einladungen laufen nach einer festgelegten Gültigkeitsdauer ab; eine konkrete
  Anzahl von Tagen ist in der SRS nicht vorgegeben und wird vor dem Start der
  Einladung festgelegt.
- Einladungen setzen eine erreichbare E-Mail-Adresse sowie einen verfügbaren
  Versandweg voraus; das konkrete Anmeldeverfahren wird in der Planung festgelegt.
- Das Entfernen eines Spiels aus dem aktiven Spielplan erfordert eine Bestätigung;
  zugehörige historische Aufgaben und Eintragungen bleiben für die
  Nachvollziehbarkeit erhalten.
- Die konkreten Spalten- und Feldschemas für Dateiimporte werden separat
  festgelegt. Die SRS-Vorgaben für Vorschau, Feldzuordnung und Validierung gelten
  unabhängig vom jeweiligen Importformat.
- Automatische Erinnerungen, weitergehende Integrationen und automatisierte
  Saisonarchivierung sind nicht Bestandteil der ersten Produktfassung.
