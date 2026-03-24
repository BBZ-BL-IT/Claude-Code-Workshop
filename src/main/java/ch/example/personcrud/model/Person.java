package ch.example.personcrud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "personen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vorname darf nicht leer sein")
    @Size(min = 2, max = 50, message = "Vorname muss zwischen 2 und 50 Zeichen lang sein")
    @Column(nullable = false)
    private String vorname;

    @NotBlank(message = "Nachname darf nicht leer sein")
    @Size(min = 2, max = 50, message = "Nachname muss zwischen 2 und 50 Zeichen lang sein")
    @Column(nullable = false)
    private String nachname;

    @NotBlank(message = "E-Mail darf nicht leer sein")
    @Email(message = "Ungültige E-Mail-Adresse")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "Geburtsdatum darf nicht leer sein")
    @Past(message = "Geburtsdatum muss in der Vergangenheit liegen")
    @Column(nullable = false)
    private LocalDate geburtsdatum;

    @Size(max = 100, message = "Adresse darf maximal 100 Zeichen lang sein")
    private String adresse;
}
