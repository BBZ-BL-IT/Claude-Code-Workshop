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

    // ─── Helper methods ───────────────────────────────────────────────────────

    private Person buildPerson(Long id) {
        return Person.builder()
                .id(id)
                .firstName("Hans")
                .lastName("Muster")
                .email("hans.muster@example.ch")
                .birthDate(LocalDate.of(1985, 3, 15))
                .build();
    }

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    void findAll_returnsAllPersonsFromRepository() {
        List<Person> persons = List.of(buildPerson(1L), buildPerson(2L));
        when(personRepository.findAll()).thenReturn(persons);

        List<Person> result = personService.findAll();

        assertThat(result).hasSize(2);
        verify(personRepository).findAll();
    }

    // ─── search ───────────────────────────────────────────────────────────────

    @Test
    void search_withNullTerm_delegatesToFindAll() {
        when(personRepository.findAll()).thenReturn(List.of(buildPerson(1L)));

        personService.search(null);

        verify(personRepository).findAll();
        verify(personRepository, never()).searchPersons(any());
    }

    @Test
    void search_withBlankTerm_delegatesToFindAll() {
        when(personRepository.findAll()).thenReturn(List.of());

        personService.search("   ");

        verify(personRepository).findAll();
        verify(personRepository, never()).searchPersons(any());
    }

    @Test
    void search_withTerm_delegatesToRepository() {
        List<Person> matches = List.of(buildPerson(1L));
        when(personRepository.searchPersons("Hans")).thenReturn(matches);

        List<Person> result = personService.search("Hans");

        assertThat(result).hasSize(1);
        verify(personRepository).searchPersons("Hans");
    }

    @Test
    void search_trimsTermBeforePassingToRepository() {
        when(personRepository.searchPersons("Hans")).thenReturn(List.of());

        personService.search("  Hans  ");

        verify(personRepository).searchPersons("Hans");
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_withValidId_returnsPerson() {
        Person person = buildPerson(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        Optional<Person> result = personService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("hans.muster@example.ch");
    }

    @Test
    void findById_withUnknownId_returnsEmpty() {
        when(personRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Person> result = personService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ─── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_delegatesToRepositoryAndReturnsSavedPerson() {
        Person person = buildPerson(null);
        Person saved = buildPerson(1L);
        when(personRepository.save(person)).thenReturn(saved);

        Person result = personService.save(person);

        assertThat(result.getId()).isEqualTo(1L);
        verify(personRepository).save(person);
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    void delete_callsDeleteById() {
        personService.delete(1L);

        verify(personRepository).deleteById(1L);
    }

    // ─── emailExists ──────────────────────────────────────────────────────────

    @Test
    void emailExists_whenPresent_returnsTrue() {
        when(personRepository.existsByEmail("hans.muster@example.ch")).thenReturn(true);

        boolean result = personService.emailExists("hans.muster@example.ch");

        assertThat(result).isTrue();
    }

    @Test
    void emailExists_whenAbsent_returnsFalse() {
        when(personRepository.existsByEmail("new@example.ch")).thenReturn(false);

        boolean result = personService.emailExists("new@example.ch");

        assertThat(result).isFalse();
    }

    // ─── isEmailTakenByOthers ──────────────────────────────────────────────────

    @Test
    void isEmailTakenByOthers_whenOtherPersonHasSameEmail_returnsTrue() {
        when(personRepository.existsByEmailAndIdNot("hans.muster@example.ch", 2L)).thenReturn(true);

        boolean result = personService.isEmailTakenByOthers("hans.muster@example.ch", 2L);

        assertThat(result).isTrue();
    }

    @Test
    void isEmailTakenByOthers_whenNoOtherPersonHasSameEmail_returnsFalse() {
        when(personRepository.existsByEmailAndIdNot("hans.muster@example.ch", 1L)).thenReturn(false);

        boolean result = personService.isEmailTakenByOthers("hans.muster@example.ch", 1L);

        assertThat(result).isFalse();
    }
}
