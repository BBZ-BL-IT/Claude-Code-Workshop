package ch.example.personcrud.controller;

import ch.example.personcrud.model.Person;
import ch.example.personcrud.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/personen")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    // ─── LIST ────────────────────────────────────────────────────────────────

    @GetMapping
    public String liste(@RequestParam(required = false) String suche, Model model) {
        List<Person> personen = (suche != null && !suche.isBlank())
                ? personService.suchePersonen(suche)
                : personService.allePersonen();
        model.addAttribute("personen", personen);
        model.addAttribute("suche", suche);
        return "person/liste";
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────

    @GetMapping("/neu")
    public String neuFormular(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("titel", "Neue Person erfassen");
        return "person/formular";
    }

    @PostMapping("/neu")
    public String neuSpeichern(@Valid @ModelAttribute("person") Person person,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        // E-Mail-Duplikat prüfen
        if (!result.hasFieldErrors("email") && personService.emailExistiert(person.getEmail())) {
            result.rejectValue("email", "duplicate", "Diese E-Mail-Adresse ist bereits vergeben");
        }

        if (result.hasErrors()) {
            model.addAttribute("titel", "Neue Person erfassen");
            return "person/formular";
        }

        personService.speichern(person);
        redirectAttributes.addFlashAttribute("erfolgMeldung",
                person.getVorname() + " " + person.getNachname() + " wurde erfolgreich gespeichert.");
        return "redirect:/personen";
    }

    // ─── READ (Detail) ────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return personService.findeById(id)
                .map(person -> {
                    model.addAttribute("person", person);
                    return "person/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("fehlerMeldung", "Person nicht gefunden.");
                    return "redirect:/personen";
                });
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @GetMapping("/{id}/bearbeiten")
    public String bearbeitenFormular(@PathVariable Long id, Model model,
                                     RedirectAttributes redirectAttributes) {
        return personService.findeById(id)
                .map(person -> {
                    model.addAttribute("person", person);
                    model.addAttribute("titel", "Person bearbeiten");
                    return "person/formular";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("fehlerMeldung", "Person nicht gefunden.");
                    return "redirect:/personen";
                });
    }

    @PostMapping("/{id}/bearbeiten")
    public String bearbeitenSpeichern(@PathVariable Long id,
                                      @Valid @ModelAttribute("person") Person person,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        person.setId(id);

        // E-Mail-Duplikat bei anderen Personen prüfen
        if (!result.hasFieldErrors("email") && personService.emailExistiertBeiAnderer(person.getEmail(), id)) {
            result.rejectValue("email", "duplicate", "Diese E-Mail-Adresse ist bereits vergeben");
        }

        if (result.hasErrors()) {
            model.addAttribute("titel", "Person bearbeiten");
            return "person/formular";
        }

        personService.speichern(person);
        redirectAttributes.addFlashAttribute("erfolgMeldung",
                person.getVorname() + " " + person.getNachname() + " wurde erfolgreich aktualisiert.");
        return "redirect:/personen";
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        personService.findeById(id).ifPresentOrElse(
                person -> {
                    personService.loeschen(id);
                    redirectAttributes.addFlashAttribute("erfolgMeldung",
                            person.getVorname() + " " + person.getNachname() + " wurde gelöscht.");
                },
                () -> redirectAttributes.addFlashAttribute("fehlerMeldung", "Person nicht gefunden.")
        );
        return "redirect:/personen";
    }
}
