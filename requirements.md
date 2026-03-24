# Anforderungen – person-crud

## Datensatz „Person"
| Feld          | Typ         | Pflicht | Regeln                                      |
|---------------|-------------|---------|---------------------------------------------|
| `vorname`     | String      | ja      | 2–50 Zeichen                                |
| `nachname`    | String      | ja      | 2–50 Zeichen                                |
| `email`       | String      | ja      | gültiges E-Mail-Format, **projektweit eindeutig** |
| `geburtsdatum`| LocalDate   | ja      | muss in der Vergangenheit liegen            |
| `adresse`     | String      | nein    | max. 100 Zeichen                            |

## CRUD-Operationen
- **Liste** (`GET /personen`) — alle Personen anzeigen; optionale Volltextsuche über `vorname`, `nachname`, `email`
- **Erstellen** (`GET/POST /personen/neu`) — neue Person erfassen
- **Detail** (`GET /personen/{id}`) — Einzelansicht (read-only)
- **Bearbeiten** (`GET/POST /personen/{id}/bearbeiten`) — bestehende Person ändern
- **Löschen** (`POST /personen/{id}/loeschen`) — Person löschen

## Validierung & Fehlermeldungen
- Alle Pflichtfelder werden serverseitig mit Bean Validation geprüft.
- Doppelte E-Mail-Adressen werden im Controller explizit abgefangen (vor dem Speichern).
- Fehler- und Erfolgsmeldungen erscheinen als Flash-Attribute auf der Listenseite.
- Beim Bearbeiten darf die eigene E-Mail-Adresse behalten werden (nur Duplikate bei *anderen* Personen werden abgelehnt).

## Startdaten
`DataInitializer` befüllt die leere Datenbank beim Start mit 4 Beispielpersonen.
