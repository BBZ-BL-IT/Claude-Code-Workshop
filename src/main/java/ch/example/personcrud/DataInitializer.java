package ch.example.personcrud;

import ch.example.personcrud.model.Person;
import ch.example.personcrud.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PersonRepository personRepository;

    @Override
    public void run(String... args) {
        if (personRepository.count() > 0) return;

        List<Person> beispielPersonen = List.of(
            Person.builder()
                .vorname("Hans").nachname("Muster")
                .email("hans.muster@example.ch")
                .geburtsdatum(LocalDate.of(1985, 3, 15))
                .adresse("Hauptstrasse 1, 4001 Basel")
                .build(),
            Person.builder()
                .vorname("Anna").nachname("Müller")
                .email("anna.mueller@example.ch")
                .geburtsdatum(LocalDate.of(1992, 7, 22))
                .adresse("Bahnhofstrasse 42, 8001 Zürich")
                .build(),
            Person.builder()
                .vorname("Peter").nachname("Schmid")
                .email("peter.schmid@example.ch")
                .geburtsdatum(LocalDate.of(1978, 11, 5))
                .adresse("Marktgasse 7, 3011 Bern")
                .build(),
            Person.builder()
                .vorname("Laura").nachname("Keller")
                .email("laura.keller@example.ch")
                .geburtsdatum(LocalDate.of(2000, 1, 30))
                .adresse(null)
                .build()
        );

        personRepository.saveAll(beispielPersonen);
        log.info("✅ {} Beispiel-Personen gespeichert.", beispielPersonen.size());
    }
}
