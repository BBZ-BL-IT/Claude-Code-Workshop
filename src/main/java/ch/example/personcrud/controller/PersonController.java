package ch.example.personcrud.controller;

import ch.example.personcrud.model.Address;
import ch.example.personcrud.model.Person;
import ch.example.personcrud.service.AddressService;
import ch.example.personcrud.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final AddressService addressService;

    // ─── LIST ────────────────────────────────────────────────────────────────

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        List<Person> persons = (search != null && !search.isBlank())
                ? personService.search(search)
                : personService.findAll();

        Map<Long, Address> firstAddresses = new HashMap<>();
        for (Person p : persons) {
            addressService.findFirstByPersonId(p.getId())
                    .ifPresent(a -> firstAddresses.put(p.getId(), a));
        }

        model.addAttribute("persons", persons);
        model.addAttribute("firstAddresses", firstAddresses);
        model.addAttribute("search", search);
        return "person/list";
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("title", "Neue Person erfassen");
        return "person/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("person") Person person,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        // Check for duplicate email
        if (!result.hasFieldErrors("email") && personService.emailExists(person.getEmail())) {
            result.rejectValue("email", "duplicate", "Diese E-Mail-Adresse ist bereits vergeben");
        }

        if (result.hasErrors()) {
            model.addAttribute("title", "Neue Person erfassen");
            return "person/form";
        }

        personService.save(person);
        redirectAttributes.addFlashAttribute("successMessage",
                person.getFirstName() + " " + person.getLastName() + " wurde erfolgreich gespeichert.");
        return "redirect:/persons";
    }

    // ─── READ (Detail) ────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return personService.findById(id)
                .map(person -> {
                    model.addAttribute("person", person);
                    return "person/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.");
                    return "redirect:/persons";
                });
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return personService.findById(id)
                .map(person -> {
                    model.addAttribute("person", person);
                    model.addAttribute("title", "Person bearbeiten");
                    return "person/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.");
                    return "redirect:/persons";
                });
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("person") Person person,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        person.setId(id);

        // Check for duplicate email among other persons
        if (!result.hasFieldErrors("email") && personService.isEmailTakenByOthers(person.getEmail(), id)) {
            result.rejectValue("email", "duplicate", "Diese E-Mail-Adresse ist bereits vergeben");
        }

        if (result.hasErrors()) {
            model.addAttribute("title", "Person bearbeiten");
            return "person/form";
        }

        personService.save(person);
        redirectAttributes.addFlashAttribute("successMessage",
                person.getFirstName() + " " + person.getLastName() + " wurde erfolgreich aktualisiert.");
        return "redirect:/persons";
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        personService.findById(id).ifPresentOrElse(
                person -> {
                    personService.delete(id);
                    redirectAttributes.addFlashAttribute("successMessage",
                            person.getFirstName() + " " + person.getLastName() + " wurde gelöscht.");
                },
                () -> redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.")
        );
        return "redirect:/persons";
    }
}