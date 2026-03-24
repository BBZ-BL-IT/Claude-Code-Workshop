package ch.example.personcrud.service;

import ch.example.personcrud.model.Person;
import ch.example.personcrud.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Person> search(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return findAll();
        }
        return personRepository.searchPersons(searchTerm.trim());
    }

    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }

    public void delete(Long id) {
        personRepository.deleteById(id);
    }

    public boolean emailExists(String email) {
        return personRepository.existsByEmail(email);
    }

    public boolean isEmailTakenByOthers(String email, Long id) {
        return personRepository.existsByEmailAndIdNot(email, id);
    }
}