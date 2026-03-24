package ch.example.personcrud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "adressen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Strasse darf nicht leer sein")
    @Size(max = 100, message = "Strasse darf maximal 100 Zeichen lang sein")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "Hausnummer darf nicht leer sein")
    @Size(max = 10, message = "Hausnummer darf maximal 10 Zeichen lang sein")
    @Column(nullable = false)
    private String houseNumber;

    @NotBlank(message = "Postleitzahl darf nicht leer sein")
    @Size(max = 10, message = "Postleitzahl darf maximal 10 Zeichen lang sein")
    @Column(nullable = false)
    private String postalCode;

    @NotBlank(message = "Ort darf nicht leer sein")
    @Size(max = 100, message = "Ort darf maximal 100 Zeichen lang sein")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Land darf nicht leer sein")
    @Size(max = 50, message = "Land darf maximal 50 Zeichen lang sein")
    @Column(nullable = false)
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    @ToString.Exclude
    private Person person;
}
