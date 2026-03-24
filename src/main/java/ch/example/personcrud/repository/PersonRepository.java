package ch.example.personcrud.repository;

import ch.example.personcrud.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("SELECT p FROM Person p WHERE " +
           "LOWER(p.vorname) LIKE LOWER(CONCAT('%', :suchbegriff, '%')) OR " +
           "LOWER(p.nachname) LIKE LOWER(CONCAT('%', :suchbegriff, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :suchbegriff, '%'))")
    List<Person> suchePersonen(@Param("suchbegriff") String suchbegriff);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
