package ch.example.personcrud.service;

import ch.example.personcrud.model.Address;
import ch.example.personcrud.model.Person;
import ch.example.personcrud.repository.AddressRepository;
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
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

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

    private Address buildAddress(Long id, Person person) {
        return Address.builder()
                .id(id)
                .street("Hauptstrasse")
                .houseNumber("1")
                .postalCode("4001")
                .city("Basel")
                .country("Schweiz")
                .person(person)
                .build();
    }

    // ─── findByPersonId ───────────────────────────────────────────────────────

    @Test
    void findByPersonId_returnsAllAddressesOfPerson() {
        Person person = buildPerson(1L);
        List<Address> addresses = List.of(buildAddress(1L, person), buildAddress(2L, person));
        when(addressRepository.findByPersonId(1L)).thenReturn(addresses);

        List<Address> result = addressService.findByPersonId(1L);

        assertThat(result).hasSize(2);
        verify(addressRepository).findByPersonId(1L);
    }

    @Test
    void findByPersonId_withNoAddresses_returnsEmptyList() {
        when(addressRepository.findByPersonId(1L)).thenReturn(List.of());

        List<Address> result = addressService.findByPersonId(1L);

        assertThat(result).isEmpty();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_withValidId_returnsAddress() {
        Person person = buildPerson(1L);
        Address address = buildAddress(10L, person);
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        Optional<Address> result = addressService.findById(10L);

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo("Basel");
    }

    @Test
    void findById_withUnknownId_returnsEmpty() {
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Address> result = addressService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ─── findFirstByPersonId ──────────────────────────────────────────────────

    @Test
    void findFirstByPersonId_returnsFirstAddress() {
        Person person = buildPerson(1L);
        Address firstAddress = buildAddress(1L, person);
        when(addressRepository.findTopByPersonIdOrderByIdAsc(1L)).thenReturn(Optional.of(firstAddress));

        Optional<Address> result = addressService.findFirstByPersonId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void findFirstByPersonId_withNoAddresses_returnsEmpty() {
        when(addressRepository.findTopByPersonIdOrderByIdAsc(1L)).thenReturn(Optional.empty());

        Optional<Address> result = addressService.findFirstByPersonId(1L);

        assertThat(result).isEmpty();
    }

    // ─── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_delegatesToRepositoryAndReturnsSavedAddress() {
        Person person = buildPerson(1L);
        Address newAddress = buildAddress(null, person);
        Address saved = buildAddress(10L, person);
        when(addressRepository.save(newAddress)).thenReturn(saved);

        Address result = addressService.save(newAddress);

        assertThat(result.getId()).isEqualTo(10L);
        verify(addressRepository).save(newAddress);
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    void delete_callsDeleteById() {
        addressService.delete(10L);

        verify(addressRepository).deleteById(10L);
    }
}
