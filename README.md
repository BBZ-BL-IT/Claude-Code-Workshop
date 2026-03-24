# 📖 Reference: Subagents

> **Branch:** `reference/subagents`  
> Dies ist eine **Musterlösung** — nicht in deinen `main` mergen!

---

## Was wurde hinzugefügt?

| Datei | Zweck |
|-------|-------|
| `.claude/agents/code-erklaerer-efz.md` | Agent, der Code auf Lernenden-Niveau erklärt (Deutsch) |
| `.claude/agents/spring-mvc-code-reviewer.md` | Agent, der Code-Reviews nach Architektur-Regeln durchführt (English) |
| `.claude/settings.local.json` | Lokale Berechtigungen (z.B. WebSearch erlauben) |

---

## Aufbau beider Agents

Beide Agents folgen dem gleichen, konsistenten Aufbau:

```
---                              ← YAML Frontmatter (Englisch)
name: agent-name
description: >
  Kurze, klare Beschreibung mit Scope-Grenzen.
tools: Read, Grep, Glob         ← Nur Lesezugriff
model: sonnet                    ← Schnell und günstig
color: green                     ← Farbe im Terminal
---

System-Prompt als Markdown       ← Sprache = gewünschte Ausgabesprache
```

### Warum Frontmatter auf Englisch, System-Prompt variabel?

- **Frontmatter** ist Metadaten — Claude nutzt `description` um zu entscheiden, wann der Agent eingesetzt wird. Englisch funktioniert hier am zuverlässigsten.
- Die **System-Prompt** bestimmt die Ausgabesprache. Der Erklärer antwortet auf Deutsch (für Lernende), der Reviewer auf Englisch (professioneller Code-Review-Stil).
- Du kannst Claude trotzdem **auf Deutsch ansprechen** — die Antwortsprache wird durch die System-Prompt gesteuert, nicht durch deine Eingabe.

---

## settings.local.json vs settings.json

| Datei | Scope | In Git? |
|-------|-------|---------|
| `settings.json` | Projekt (alle Entwickler) | ✅ Ja |
| `settings.local.json` | Nur du (persönlich) | ❌ Nein (in .gitignore) |

---

→ Zurück zum Workshop-Überblick (`main` branch)
