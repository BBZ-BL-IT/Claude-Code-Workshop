package ch.example.personcrud.service;

import ch.example.personcrud.model.Address;
import ch.example.personcrud.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public List<Address> findByPersonId(Long personId) {
        return addressRepository.findByPersonId(personId);
    }

    @Transactional(readOnly = true)
    public Optional<Address> findFirstByPersonId(Long personId) {
        return addressRepository.findTopByPersonIdOrderByIdAsc(personId);
    }

    @Transactional(readOnly = true)
    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    public Address save(Address address) {
        return addressRepository.save(address);
    }

    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}
