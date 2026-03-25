# 🛠️ Workshop: Claude Code in der Praxis

📊 [Workshop-Präsentation (PDF)](docs/workshop-presentation.pdf)
 
> **Voraussetzungen:** IntelliJ IDEA installiert, aktives Claude-Abo (Pro/Team), Java 21, Maven

---

## 📋 Überblick

In diesem Workshop arbeiten wir mit einer bestehenden **Spring Boot MVC Applikation** (Person-CRUD) und integrieren Schritt für Schritt Claude Code als KI-gestütztes Entwicklungswerkzeug.

### Was wir behandeln

| Block | Thema | Reference-Branch |
|-------|-------|-------------------|
| 0 | Setup & App kennenlernen | — |
| 1 | Projekt initialisieren (`/init`, CLAUDE.md, Tests) | `reference/init` |
| 2 | Subagents einrichten | `reference/subagents` |
| 3 | Skills erstellen | `reference/skills` |
| 4 | Hooks konfigurieren | `reference/hooks` |
| 5 | Wrap-up & Ausblick | — |

---

## 🚀 Setup (Block 0)

### 1. Claude Code CLI installieren

```bash
# macOS / Linux
npm install -g @anthropic-ai/claude-code

# Überprüfen
claude --version
```

> **Windows-Nutzer:** Claude Code benötigt WSL2. Siehe die [offizielle Dokumentation](https://code.claude.com/docs/de/overview#terminal) für Details.

### 2. Claude Code in IntelliJ integrieren

Claude Code lässt sich direkt als Plugin in JetBrains IDEs verwenden:

1. Öffne IntelliJ IDEA → **Settings** → **Plugins** → **Marketplace**
2. Suche nach **"Claude Code"** und installiere das Plugin
3. Starte IntelliJ neu
4. Das Claude Code Terminal-Fenster ist jetzt über **View → Tool Windows → Claude Code** erreichbar

Alternativ kannst du Claude Code auch direkt im **IntelliJ Terminal-Tab** verwenden, indem du einfach `claude` eingibst.

> Weitere Details zur JetBrains-Integration findest du in der [offiziellen Doku](https://code.claude.com/docs/de/overview#jetbrains).

### 3. Repository klonen

```bash
git clone <REPO-URL>
cd person-crud-workshop
```

### 4. App starten und testen

```bash
mvn spring-boot:run
```

Öffne [http://localhost:8085](http://localhost:8085) — du solltest eine Personenliste mit 4 Einträgen sehen.

### 5. Claude Code starten

```bash
# Im Projektverzeichnis (Terminal oder IntelliJ Claude Code Fenster):
claude
```

Claude Code öffnet sich. Du kannst jetzt direkt mit Claude interagieren.

---

## 🔀 Wie wir mit Branches arbeiten

Deine **eigene Arbeit** passiert immer auf dem `main`-Branch. Du probierst alles selbst aus und baust dein Setup schrittweise auf.

Für jeden Themenblock gibt es einen **`reference/`-Branch** mit einer Musterlösung. Du kannst jederzeit reinschauen:

```bash
# Musterlösung für Block 1 ansehen (ohne deinen main zu verlieren)
git stash                          # deine Änderungen zwischenspeichern
git checkout reference/init        # Musterlösung ansehen
# ... anschauen, lernen ...
git checkout main                  # zurück zu deiner Arbeit
git stash pop                      # deine Änderungen wiederherstellen
```

Oder direkt auf GitHub den Branch wechseln und die Dateien online lesen.

> ⚠️ **Wichtig:** Merge die reference-Branches **nicht** in deinen main — sie dienen nur als Nachschlagewerk.

---

## 📘 Block 1 — Projekt initialisieren

### Was ist `/init`?

Wenn du in Claude Code `/init` eingibst, analysiert Claude dein Projekt und erstellt automatisch eine `CLAUDE.md`-Datei. Diese Datei ist wie ein "Briefing" für Claude — sie beschreibt:

- Welche Befehle es gibt (Build, Run, Test)
- Wie die Architektur aufgebaut ist
- Welche Konventionen gelten

### Warum auch `requirements.md`?

Die `CLAUDE.md` beschreibt das **technische Setup** (Wie ist das Projekt aufgebaut? Welche Befehle gibt es?). Eine separate `requirements.md` beschreibt die **fachlichen Anforderungen** (Was soll die Applikation tun? Welche Daten werden verwaltet? Welche Geschäftsregeln gelten?). So kann Claude bei jedem Gespräch die vollständige Spezifikation lesen.

Wichtig: In `requirements.md` gehören **nur fachliche Anforderungen** — keine technischen Details wie Stack, Ports oder Architektur-Regeln. Diese stehen bereits in `CLAUDE.md`.

**Ziel:** Claude Code kennt dein Projekt und kann gezielt helfen. Bestehender Code wird mit Unit-Tests abgesichert.

### Aufgabe

1. Starte Claude Code im Projektverzeichnis: `claude`
2. Gib `/init` ein und lass Claude die `CLAUDE.md` generieren
3. Schau dir das Ergebnis an — was hat Claude erkannt? Was fehlt?
4. Ergänze die `CLAUDE.md` manuell:
   - Beschreibe die 3-Layer-Architektur (Controller → Service → Repository)
   - Füge eine **Testing-Sektion** hinzu: Nach Abschluss von Arbeiten soll `mvn test` ausgeführt werden. Falls keine Tests für eine geänderte Service-Klasse vorhanden sind, soll Claude fragen, ob Unit-Tests generiert werden sollen (Mockito, Repository gemockt).
5. Erstelle manuell eine `requirements.md` mit den fachlichen Anforderungen
6. **Bitte Claude, Unit-Tests für den `PersonService` zu generieren.** Die Repository-Schicht soll dabei mit Mockito gemockt werden (kein `@SpringBootTest`). Verifiziere mit `mvn test`.

### 💡 Tipps

- Claude liest `CLAUDE.md` automatisch bei jeder neuen Konversation
- In `CLAUDE.md` kannst du Claude anweisen, auch `requirements.md` zu lesen (z.B. mit einem Hinweis am Anfang der Datei)
- Halte beide Dateien aktuell — sie sind dein "Projektvertrag" mit Claude
- Best Practice: `CLAUDE.md` soll **kurz und prägnant** sein — nur das, was Claude wirklich wissen muss. Alles andere gehört in Skills oder die Requirements.

### Musterlösung

→ Branch `reference/init` enthält eine ausgearbeitete `CLAUDE.md`, `requirements.md` und einen `PersonServiceTest`

---

## 📘 Block 2 — Subagents

### Was sind Subagents?

Subagents sind **spezialisierte Claude-Instanzen** mit einem eigenen Auftrag. Sie werden als Markdown-Dateien im Ordner `.claude/agents/` abgelegt. Jeder Agent hat:

- Einen **Namen** und eine **Beschreibung** (wann soll er eingesetzt werden?)
- Erlaubte **Tools** (z.B. nur Lesen, kein Schreiben)
- Ein **Modell** (sonnet = schnell/günstig, opus = stärker)
- Eine **System-Prompt** mit seinen Regeln und seinem Ausgabeformat

**Ziel:** Spezialisierte KI-Agenten für wiederkehrende Aufgaben einrichten.

### Aufgabe: Schritt für Schritt einen Subagent erstellen

**Vorbereitung:**
```bash
mkdir -p .claude/agents
```

**Schritt 1 — Frontmatter definieren**

Erstelle eine Datei `.claude/agents/spring-mvc-code-reviewer.md` und beginne mit dem YAML-Frontmatter:

```yaml
---
name: spring-mvc-code-reviewer
description: >
  Reviews code for architecture compliance and best practices.
  Trigger after implementing a new feature or entity. Read-only.
tools: Read, Grep, Glob
model: sonnet
color: blue
---
```

**Schritt 2 — System-Prompt schreiben**

Unterhalb des Frontmatters kommt die System-Prompt als Markdown. Definiere:
- Die **Rolle** des Agents (z.B. "erfahrener Java-Entwickler, der Code reviewt")
- **Pflichtschritte** (z.B. "Lies zuerst CLAUDE.md und requirements.md")
- **Was geprüft wird** (z.B. Layer-Compliance, Annotations, Validierung)
- Ein **festes Ausgabeformat** (z.B. Summary → Findings → What's Done Well)

**Schritt 3 — Testen**

Frage Claude: *"Review den PersonController auf Layer-Violations"* — Claude sollte den Agent automatisch erkennen und starten.

**Bonus:** Erstelle einen zweiten Agent `code-erklaerer-efz.md`, der Code auf Lernenden-Niveau (2. Lehrjahr) auf Deutsch erklärt.

### 💡 Tipps

- Agents mit `tools: Read, Grep, Glob` sind read-only und können nichts kaputt machen
- Die **Beschreibung** im Frontmatter bestimmt, wann Claude den Agent automatisch einsetzt — schreibe sie klar und mit Scope-Grenzen
- Ein fixes **Ausgabeformat** sorgt für konsistente Ergebnisse
- Die Sprache der System-Prompt sollte der gewünschten **Ausgabesprache** entsprechen

### Musterlösung

→ Branch `reference/subagents` enthält zwei ausgearbeitete Agents (Code-Reviewer + Code-Erklärer)

---

## 📘 Block 3 — Skills

### Was sind Skills?

Skills sind **detaillierte Anleitungen** für wiederkehrende Aufgaben. Sie werden in `.claude/skills/<n>/SKILL.md` abgelegt. Während Subagents eigenständige Agenten sind, sind Skills eher wie Kochrezepte — sie beschreiben Schritt für Schritt, wie eine bestimmte Aufgabe auszuführen ist.

### Unterschied Agent vs. Skill

| | Agent | Skill |
|---|---|---|
| **Zweck** | Eigenständige Rolle (z.B. Reviewer) | Rezept für eine Aufgabe (z.B. "Entity scaffolden") |
| **Ort** | `.claude/agents/` | `.claude/skills/<n>/SKILL.md` |
| **Hat eigenes Modell?** | Ja (`model: sonnet`) | Nein (nutzt das aktuelle Modell) |
| **Hat eigene Tools?** | Ja (`tools: Read, Glob`) | Ja (`allowed-tools: Read, Write, Edit`) |
| **Typischer Einsatz** | Review, Erklärung, Analyse | Code-Generierung, Scaffolding, Migration |

**Ziel:** Ein wiederverwendbares "Rezept" erstellen, mit dem Claude eine neue Entity inkl. aller Schichten scaffolden kann.

### Aufgabe

1. Erstelle den Ordner: `mkdir -p .claude/skills/entity-scaffold`
2. Erstelle eine `SKILL.md` mit folgendem Aufbau — der Skill soll Claude ermöglichen, eine **komplette neue CRUD-Entity** (Model → Repository → Service → Controller → Templates → DataInitializer → Navigation) in einem Durchgang zu erstellen, konsistent mit dem bestehenden Code-Stil.
3. Deine `SKILL.md` sollte folgende Schritte enthalten:

| Step | Was Claude tun soll |
|------|---------------------|
| **Step 0 — Verstehen** | Klärungsfragen an den User: Entity-Name, Felder, Beziehungen, spezielle Regeln |
| **Step 1 — Bestehenden Code lesen** | Bestehende Entity, Repository, Service, Controller und Templates lesen, um den Code-Stil zu lernen |
| **Step 2 — Entity generieren** | JPA-Entity in `model/` mit Lombok, Bean Validation, Beziehungen |
| **Step 3 — Repository generieren** | Spring Data Interface in `repository/` mit Suchabfragen |
| **Step 4 — Service generieren** | `@Service` + `@Transactional` in `service/` — dünne Schicht |
| **Step 5 — Controller generieren** | `@Controller` in `controller/` mit URL-Schema, Flash-Messages, Validierung |
| **Step 6 — Templates generieren** | Drei Thymeleaf-Templates: list, form, detail in `templates/<entity>/` |
| **Step 7 — DataInitializer** | 3–5 Beispieldatensätze hinzufügen |
| **Step 8 — Navigation** | Link in `layout.html` ergänzen |
| **Step 9 — Verifizieren** | Imports prüfen, Layer-Violations ausschliessen, `mvn compile` |

4. **Testen:** Bitte Claude, eine neue Entity "Address" anzulegen mit den Feldern: street, houseNumber, postalCode, city, country (alle required) — als `@ManyToOne`-Beziehung zu Person.

### 💡 Tipps

- Der wichtigste Step ist **Step 1 — Bestehenden Code lesen!** Ohne diesen Schritt generiert Claude generischen Code. Mit diesem Schritt sieht der generierte Code aus, als hätte der gleiche Entwickler ihn geschrieben.
- Skills dürfen schreiben (`allowed-tools: Read, Write, Edit, Grep, Glob`) — anders als read-only Agents.

### Musterlösung

→ Branch `reference/skills` enthält den ausgearbeiteten Skill **und** die damit generierte Address-Entity

---

## 📘 Block 4 — Hooks

### Was sind Hooks?

Hooks sind **Shell-Skripte**, die automatisch vor oder nach bestimmten Claude-Aktionen ausgeführt werden. Sie funktionieren ähnlich wie Git-Hooks — z.B. kannst du verhindern, dass Claude bestimmte Dateien überschreibt.

### Hook-Typen

| Hook | Wann? | Typischer Einsatz |
|------|-------|-------------------|
| `PreToolUse` | Bevor Claude ein Tool benutzt | Dateien schützen, Aktionen blockieren |
| `PostToolUse` | Nachdem Claude ein Tool benutzt hat | Linting, Formatierung |
| `Notification` | Bei bestimmten Events | Benachrichtigungen |

### Exit-Codes

| Code | Bedeutung |
|------|-----------|
| `exit 0` | Aktion erlauben |
| `exit 1` | Hook-Fehler (Hook defekt) — Aktion wird trotzdem erlaubt |
| `exit 2` | Aktion **blockieren** — Claude sieht die Fehlermeldung |

**Ziel:** Automatische Schutzmechanismen für sensible Dateien einrichten.

### Aufgabe

1. **Lass Claude den Hook erstellen:** Bitte Claude, ein Shell-Skript `.claude/hooks/protect-config.sh` zu schreiben, das verhindert, dass `application.properties` geändert wird. Erkläre Claude das gewünschte Verhalten und die Exit-Codes.
2. Registriere den Hook in `.claude/settings.json`:
   ```json
   {
     "hooks": {
       "PreToolUse": [
         {
           "matcher": "Write|Edit",
           "hooks": [
             {
               "type": "command",
               "command": "bash $CLAUDE_PROJECT_DIR/.claude/hooks/protect-config.sh",
               "timeout": 5
             }
           ]
         }
       ]
     }
   }
   ```
3. **Teste:** Bitte Claude, den Port in `application.properties` zu ändern — es sollte blockiert werden.

### Musterlösung

→ Branch `reference/hooks` enthält einen funktionierenden Config-Schutz-Hook

---

## 🔄 Alternativen zu Claude Code

Die Konzepte aus diesem Workshop (Projekt-Kontext, spezialisierte Agenten, wiederverwendbare Anleitungen) existieren auch in anderen Tools:

| Konzept | Claude Code | GitHub Copilot | OpenAI Codex |
|---------|-------------|----------------|--------------|
| Projekt-Kontext | `CLAUDE.md` | `.github/copilot-instructions.md` | `AGENTS.md` |
| Agenten | `.claude/agents/*.md` | `.github/agents/*.agent.md` | Codex Subagents |
| Skills/Rezepte | `.claude/skills/` | Skills (via awesome-copilot) | `.codex/skills/` (SKILL.md) |
| Hooks | `.claude/hooks/` | Agent Hooks (Preview) | `userpromptsubmit` Hook |
| Standard-Format | `CLAUDE.md` (Anthropic) | `AGENTS.md` (Open Standard) | `AGENTS.md` (Open Standard) |

> **AGENTS.md** ist ein offenes Format unter der Linux Foundation (Agentic AI Foundation), das von OpenAI Codex, GitHub Copilot, Google Jules, Cursor und anderen unterstützt wird. Claude Code liest ebenfalls `AGENTS.md`-Dateien. Die Grundidee ist überall gleich: **Je besser du dem KI-Tool dein Projekt erklärst, desto besser ist das Ergebnis.**

---

## 💡 Bonus: Security Review

In der Workshop-Applikation wurde **bewusst kein Augenmerk auf Sicherheit** gelegt. Das bietet die Gelegenheit, nach dem Workshop Sicherheitsaspekte mit KI-Unterstützung zu analysieren.

### Integrierte Analyse

Claude Code bringt den Befehl **`/security-review`** als eingebautes Bundled Skill mit. Der Befehl nutzt einen spezialisierten Security-Prompt, der Claude anweist, die Codebasis systematisch auf gängige Schwachstellen zu prüfen — darunter SQL Injection, XSS, Authentifizierungsfehler, unsichere Datenverarbeitung, hartcodierte Secrets und Dependency-Schwachstellen. Gefundene Probleme werden mit Erklärung und Severity ausgegeben, und Claude kann anschliessend direkt Fixes implementieren.

> **Voraussetzung:** Der Befehl funktioniert nur innerhalb eines Git-Repositorys. Er nutzt intern Git-Befehle wie `git status` und `git diff`, um den aktuellen Zustand des Codes und die letzten Änderungen zu erfassen. So kann Claude gezielt die relevanten Dateien analysieren, statt blind die gesamte Codebasis zu durchsuchen.

### Zusätzliche Commands installieren

Der Funktionsumfang von Claude Code lässt sich durch **Custom Commands** erweitern. Diese werden als Markdown-Dateien in `.claude/commands/` abgelegt und sind dann als Slash-Commands verfügbar. Ein Beispiel aus der Community ist [`/security-audit`](https://github.com/afiqiqmal/claude-security-audit) — ein erweiterter Sicherheits-Audit mit strukturiertem Report, der sich einfach ins Projekt installieren lässt.

---

## 📚 Weiterführende Links

- [Claude Code Dokumentation](https://code.claude.com/docs/de/overview)
- [Claude Code Best Practices](https://code.claude.com/docs/en/best-practices)
- [AGENTS.md Spezifikation](https://agents.md/)
- [OpenAI Codex Skills](https://developers.openai.com/codex/skills)
- [GitHub Copilot Custom Agents](https://docs.github.com/en/copilot/how-tos/use-copilot-agents/coding-agent/create-custom-agents)

---

*Workshop erstellt für die interne Weiterbildung — viel Spass beim Experimentieren!* 🚀
