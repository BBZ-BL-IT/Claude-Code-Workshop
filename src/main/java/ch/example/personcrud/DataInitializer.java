package ch.example.personcrud;

import ch.example.personcrud.model.Address;
import ch.example.personcrud.model.Person;
import ch.example.personcrud.repository.AddressRepository;
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
    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) {
        if (personRepository.count() > 0) return;

        List<Person> samplePersons = List.of(
            Person.builder()
                .firstName("Hans").lastName("Muster")
                .email("hans.muster@example.ch")
                .birthDate(LocalDate.of(1985, 3, 15))
                .build(),
            Person.builder()
                .firstName("Anna").lastName("Müller")
                .email("anna.mueller@example.ch")
                .birthDate(LocalDate.of(1992, 7, 22))
                .build(),
            Person.builder()
                .firstName("Peter").lastName("Schmid")
                .email("peter.schmid@example.ch")
                .birthDate(LocalDate.of(1978, 11, 5))
                .build(),
            Person.builder()
                .firstName("Laura").lastName("Keller")
                .email("laura.keller@example.ch")
                .birthDate(LocalDate.of(2000, 1, 30))
                .build()
        );

        List<Person> savedPersons = personRepository.saveAll(samplePersons);
        log.info("✅ {} sample persons saved.", savedPersons.size());

        List<Address> sampleAddresses = List.of(
            Address.builder()
                .street("Hauptstrasse").houseNumber("1")
                .postalCode("4001").city("Basel").country("Schweiz")
                .person(savedPersons.get(0)).build(),
            Address.builder()
                .street("Riehenstrasse").houseNumber("10")
                .postalCode("4058").city("Basel").country("Schweiz")
                .person(savedPersons.get(0)).build(),
            Address.builder()
                .street("Bahnhofstrasse").houseNumber("42")
                .postalCode("8001").city("Zürich").country("Schweiz")
                .person(savedPersons.get(1)).build(),
            Address.builder()
                .street("Marktgasse").houseNumber("7")
                .postalCode("3011").city("Bern").country("Schweiz")
                .person(savedPersons.get(2)).build()
        );

        addressRepository.saveAll(sampleAddresses);
        log.info("✅ {} sample addresses saved.", sampleAddresses.size());
    }
}