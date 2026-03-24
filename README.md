# 📖 Reference: Hooks

> **Branch:** `reference/hooks`  
> Dies ist eine **Musterlösung** — nicht in deinen `main` mergen!

---

## Was wurde hinzugefügt?

| Datei | Zweck |
|-------|-------|
| `.claude/hooks/protect-config.sh` | Shell-Skript, das Config-Dateien vor Änderungen schützt |
| `.claude/settings.json` | Registrierung des Hooks als PreToolUse |

---

## Wie funktioniert der Hook?

### Ablauf

```
Claude will application.properties ändern
  → PreToolUse-Hook wird ausgelöst
    → protect-config.sh prüft den Dateinamen
      → "application.properties" erkannt → exit 2 (BLOCKIERT)
      → Andere Datei → exit 0 (ERLAUBT)
```

### Exit-Codes

| Code | Bedeutung |
|------|-----------|
| `exit 0` | Aktion erlauben |
| `exit 1` | Fehler im Hook selbst — Aktion wird trotzdem erlaubt |
| `exit 2` | Aktion **blockieren** — Claude sieht die Fehlermeldung |

### Hook registrieren (`.claude/settings.json`)

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

---

## Testen

Starte Claude Code und versuche:

```
Ändere den Port in application.properties auf 9090
```

Claude sollte die Blockierung sehen und die Fehlermeldung anzeigen.

---

→ Zurück zum Workshop-Überblick (`main` branch)
