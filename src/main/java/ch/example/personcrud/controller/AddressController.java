package ch.example.personcrud.controller;

import ch.example.personcrud.model.Address;
import ch.example.personcrud.service.AddressService;
import ch.example.personcrud.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/persons/{personId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final PersonService personService;

    // ─── LIST ────────────────────────────────────────────────────────────────

    @GetMapping
    public String list(@PathVariable Long personId, Model model,
                       RedirectAttributes redirectAttributes) {
        return personService.findById(personId)
                .map(person -> {
                    model.addAttribute("person", person);
                    model.addAttribute("addresses", addressService.findByPersonId(personId));
                    return "address/list";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.");
                    return "redirect:/persons";
                });
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String createForm(@PathVariable Long personId, Model model,
                             RedirectAttributes redirectAttributes) {
        return personService.findById(personId)
                .map(person -> {
                    model.addAttribute("person", person);
                    model.addAttribute("address", new Address());
                    model.addAttribute("title", "Neue Adresse erfassen");
                    return "address/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.");
                    return "redirect:/persons";
                });
    }

    @PostMapping("/new")
    public String create(@PathVariable Long personId,
                         @Valid @ModelAttribute("address") Address address,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        return personService.findById(personId)
                .map(person -> {
                    if (result.hasErrors()) {
                        model.addAttribute("person", person);
                        model.addAttribute("title", "Neue Adresse erfassen");
                        return "address/form";
                    }
                    address.setPerson(person);
                    addressService.save(address);
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Adresse wurde erfolgreich gespeichert.");
                    return "redirect:/persons/" + personId + "/addresses";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.");
                    return "redirect:/persons";
                });
    }

    // ─── READ (Detail) ────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detail(@PathVariable Long personId, @PathVariable Long id,
                         Model model, RedirectAttributes redirectAttributes) {
        return personService.findById(personId)
                .map(person -> addressService.findById(id)
                        .map(address -> {
                            model.addAttribute("person", person);
                            model.addAttribute("address", address);
                            return "address/detail";
                        })
                        .orElseGet(() -> {
                            redirectAttributes.addFlashAttribute("errorMessage", "Adresse nicht gefunden.");
                            return "redirect:/persons/" + personId + "/addresses";
                        }))
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.");
                    return "redirect:/persons";
                });
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long personId, @PathVariable Long id,
                           Model model, RedirectAttributes redirectAttributes) {
        return personService.findById(personId)
                .map(person -> addressService.findById(id)
                        .map(address -> {
                            model.addAttribute("person", person);
                            model.addAttribute("address", address);
                            model.addAttribute("title", "Adresse bearbeiten");
                            return "address/form";
                        })
                        .orElseGet(() -> {
                            redirectAttributes.addFlashAttribute("errorMessage", "Adresse nicht gefunden.");
                            return "redirect:/persons/" + personId + "/addresses";
                        }))
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.");
                    return "redirect:/persons";
                });
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long personId, @PathVariable Long id,
                         @Valid @ModelAttribute("address") Address address,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        return personService.findById(personId)
                .map(person -> {
                    if (result.hasErrors()) {
                        model.addAttribute("person", person);
                        model.addAttribute("title", "Adresse bearbeiten");
                        return "address/form";
                    }
                    address.setId(id);
                    address.setPerson(person);
                    addressService.save(address);
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Adresse wurde erfolgreich aktualisiert.");
                    return "redirect:/persons/" + personId + "/addresses";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.");
                    return "redirect:/persons";
                });
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long personId, @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        personService.findById(personId).ifPresentOrElse(
                person -> addressService.findById(id).ifPresentOrElse(
                        address -> {
                            addressService.delete(id);
                            redirectAttributes.addFlashAttribute("successMessage",
                                    "Adresse wurde gelöscht.");
                        },
                        () -> redirectAttributes.addFlashAttribute("errorMessage", "Adresse nicht gefunden.")
                ),
                () -> redirectAttributes.addFlashAttribute("errorMessage", "Person nicht gefunden.")
        );
        return "redirect:/persons/" + personId + "/addresses";
    }
}