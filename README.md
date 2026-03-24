# 📖 Reference: Projekt initialisieren (`/init`)

> **Branch:** `reference/init`  
> Dies ist eine **Musterlösung** — nicht in deinen `main` mergen!

---

## Was wurde hinzugefügt?

| Datei | Zweck |
|-------|-------|
| `CLAUDE.md` | Technisches Briefing für Claude Code inkl. Testing-Regeln |
| `requirements.md` | Fachliche Anforderungen des Projekts |
| `src/test/.../PersonServiceTest.java` | Unit-Tests für PersonService (Mockito, Repository gemockt) |

---

## CLAUDE.md — Aufbau erklärt

Die `CLAUDE.md` enthält fünf Hauptbereiche:

### 1. Verweis auf Requirements
```markdown
> **Requirements:** Read [`requirements.md`](./requirements.md) at the start of every conversation
```
So liest Claude bei **jedem** Gesprächsstart automatisch auch die fachlichen Anforderungen.

### 2. Commands
Alle wichtigen Maven-Befehle, damit Claude die App bauen, starten und testen kann.

### 3. Architecture
Die 3-Layer-Architektur wird **explizit** beschrieben: Controller → Service → Repository.

### 4. Key Configuration
Port, Datenbank-URL, Thymeleaf-Einstellungen.

### 5. Testing (neu!)
Klare Anweisungen für Claude:
- Nach Code-Änderungen immer `mvn test` ausführen
- Wenn Tests fehlen, **fragen** ob welche generiert werden sollen
- Pattern: JUnit 5 + Mockito, Repository-Layer mocken, kein `@SpringBootTest`
- Verweis auf `PersonServiceTest` als Referenz-Implementierung

---

## PersonServiceTest — Warum so?

Der Test zeigt das empfohlene Pattern:
- **`@ExtendWith(MockitoExtension.class)`** statt `@SpringBootTest` — schneller, isolierter
- **`@Mock`** auf dem Repository — wir testen nur die Service-Logik
- **`@InjectMocks`** injiziert die Mocks automatisch
- Jede public Methode des Service hat mindestens einen Test

---

## Tipps für eigene Projekte

1. **Halte CLAUDE.md kurz** — nur das, was universell gilt. Bloated CLAUDE.md-Dateien führen dazu, dass Claude Regeln ignoriert.
2. **Sei spezifisch** bei Architektur-Regeln: "Controller darf nicht direkt auf Repository zugreifen" ist besser als "saubere Architektur verwenden"
3. **Testing-Regeln in CLAUDE.md** sorgen dafür, dass Claude automatisch Tests einfordert
4. **Sprache:** Englisch für CLAUDE.md ist empfohlen (Claude versteht es am besten)

---

→ Zurück zum Workshop-Überblick (`main` branch)
