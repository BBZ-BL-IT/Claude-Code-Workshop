# 📖 Reference: Skills

> **Branch:** `reference/skills`  
> Dies ist eine **Musterlösung** — nicht in deinen `main` mergen!

---

## Was wurde hinzugefügt?

| Datei/Ordner | Zweck |
|--------------|-------|
| `.claude/skills/entity-scaffold/SKILL.md` | Skill zum Generieren einer kompletten CRUD-Entity |
| `src/.../model/Address.java` | Generierte Entity (Ergebnis des Skills) |
| `src/.../repository/AddressRepository.java` | Generiertes Repository |
| `src/.../service/AddressService.java` | Generierter Service |
| `src/.../controller/AddressController.java` | Generierter Controller |
| `src/.../templates/address/*.html` | Generierte Templates (list, form, detail) |
| Aktualisierter `DataInitializer.java` | + Beispiel-Adressen |
| Aktualisierte `layout.html` | + Navigation zu Adressen |

---

## Der Entity-Scaffold Skill

Dieser Skill wurde verwendet, um die Address-Entity zu generieren. Der wichtigste Aspekt ist **Step 1: Bestehenden Code lesen** — Claude lernt den Code-Stil, bevor neuer Code generiert wird.

### Warum "Bestehenden Code lesen"?

Ohne diesen Schritt generiert Claude generischen Spring Boot Code. Mit diesem Schritt:
- Naming-Konventionen werden eingehalten
- URL-Patterns sind konsistent
- Flash-Message-Variablen heissen gleich
- Template-Strukturen sind identisch aufgebaut

---

## Beispiel-Aufruf

```
Erstelle eine neue Entity "Address" mit den Feldern:
- street (String, required, max 100)
- houseNumber (String, required, max 10)
- postalCode (String, required, max 10)
- city (String, required, max 100)
- country (String, required, max 50)
Beziehung: Eine Person hat viele Addresses (@OneToMany)
```

---

→ Zurück zum Workshop-Überblick (`main` branch)
