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
    public List<Person> allePersonen() {
        return personRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Person> suchePersonen(String suchbegriff) {
        if (suchbegriff == null || suchbegriff.isBlank()) {
            return allePersonen();
        }
        return personRepository.suchePersonen(suchbegriff.trim());
    }

    @Transactional(readOnly = true)
    public Optional<Person> findeById(Long id) {
        return personRepository.findById(id);
    }

    public Person speichern(Person person) {
        return personRepository.save(person);
    }

    public void loeschen(Long id) {
        personRepository.deleteById(id);
    }

    public boolean emailExistiert(String email) {
        return personRepository.existsByEmail(email);
    }

    public boolean emailExistiertBeiAnderer(String email, Long id) {
        return personRepository.existsByEmailAndIdNot(email, id);
    }
}
