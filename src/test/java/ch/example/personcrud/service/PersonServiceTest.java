package ch.example.personcrud.service;

import ch.example.personcrud.model.Person;
import ch.example.personcrud.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    // ─── Helper ──────────────────────────────────────────────────────────────

    private Person buildPerson(Long id) {
        return Person.builder()
                .id(id)
                .vorname("Hans")
                .nachname("Muster")
                .email("hans.muster@example.ch")
                .geburtsdatum(LocalDate.of(1985, 3, 15))
                .adresse("Hauptstrasse 1, 4001 Basel")
                .build();
    }

    // ─── allePersonen ────────────────────────────────────────────────────────

    @Test
    void allePersonen_gibtAllePersonenZurueck() {
        List<Person> personen = List.of(buildPerson(1L), buildPerson(2L));
        when(personRepository.findAll()).thenReturn(personen);

        List<Person> ergebnis = personService.allePersonen();

        assertThat(ergebnis).hasSize(2);
        verify(personRepository).findAll();
    }

    // ─── suchePersonen ───────────────────────────────────────────────────────

    @Test
    void suchePersonen_mitNull_delegiertAnAllePersonen() {
        when(personRepository.findAll()).thenReturn(List.of(buildPerson(1L)));

        personService.suchePersonen(null);

        verify(personRepository).findAll();
        verify(personRepository, never()).suchePersonen(any());
    }

    @Test
    void suchePersonen_mitLeeremText_delegiertAnAllePersonen() {
        when(personRepository.findAll()).thenReturn(List.of());

        personService.suchePersonen("   ");

        verify(personRepository).findAll();
        verify(personRepository, never()).suchePersonen(any());
    }

    @Test
    void suchePersonen_mitSuchbegriff_delegiertAnRepository() {
        List<Person> treffer = List.of(buildPerson(1L));
        when(personRepository.suchePersonen("Hans")).thenReturn(treffer);

        List<Person> ergebnis = personService.suchePersonen("Hans");

        assertThat(ergebnis).hasSize(1);
        verify(personRepository).suchePersonen("Hans");
    }

    // ─── findeById ───────────────────────────────────────────────────────────

    @Test
    void findeById_mitGueltigerId_gibtPersonZurueck() {
        Person person = buildPerson(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        Optional<Person> ergebnis = personService.findeById(1L);

        assertThat(ergebnis).isPresent();
        assertThat(ergebnis.get().getEmail()).isEqualTo("hans.muster@example.ch");
    }

    @Test
    void findeById_mitUnbekannterID_gibtLeerZurueck() {
        when(personRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Person> ergebnis = personService.findeById(99L);

        assertThat(ergebnis).isEmpty();
    }

    // ─── speichern ───────────────────────────────────────────────────────────

    @Test
    void speichern_delegiertAnRepository() {
        Person person = buildPerson(null);
        Person gespeichert = buildPerson(1L);
        when(personRepository.save(person)).thenReturn(gespeichert);

        Person ergebnis = personService.speichern(person);

        assertThat(ergebnis.getId()).isEqualTo(1L);
        verify(personRepository).save(person);
    }

    // ─── loeschen ────────────────────────────────────────────────────────────

    @Test
    void loeschen_ruftDeleteByIdAuf() {
        personService.loeschen(1L);

        verify(personRepository).deleteById(1L);
    }

    // ─── emailExistiert ──────────────────────────────────────────────────────

    @Test
    void emailExistiert_wennVorhanden_gibtTrueZurueck() {
        when(personRepository.existsByEmail("hans.muster@example.ch")).thenReturn(true);

        assertThat(personService.emailExistiert("hans.muster@example.ch")).isTrue();
    }

    @Test
    void emailExistiert_wennNichtVorhanden_gibtFalseZurueck() {
        when(personRepository.existsByEmail("neu@example.ch")).thenReturn(false);

        assertThat(personService.emailExistiert("neu@example.ch")).isFalse();
    }

    // ─── emailExistiertBeiAnderer ────────────────────────────────────────────

    @Test
    void emailExistiertBeiAnderer_wennBereitsVergeben_gibtTrueZurueck() {
        when(personRepository.existsByEmailAndIdNot("hans.muster@example.ch", 2L)).thenReturn(true);

        assertThat(personService.emailExistiertBeiAnderer("hans.muster@example.ch", 2L)).isTrue();
    }

    @Test
    void emailExistiertBeiAnderer_wennNichtVergeben_gibtFalseZurueck() {
        when(personRepository.existsByEmailAndIdNot("hans.muster@example.ch", 1L)).thenReturn(false);

        assertThat(personService.emailExistiertBeiAnderer("hans.muster@example.ch", 1L)).isFalse();
    }
}
