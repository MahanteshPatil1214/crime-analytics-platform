package gov.lawenforcement.person.service;

import gov.lawenforcement.person.entity.Person;
import gov.lawenforcement.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional(readOnly = true)
    public Person getPerson(UUID id) {
        return personRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Person not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Person> searchPersons(String name, Person.PersonType personType,
                                       Double minRisk, Double maxRisk,
                                       Pageable pageable) {
        String personTypeStr = personType != null ? personType.name() : null;
        return personRepository.search(name, personTypeStr, minRisk, maxRisk, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Person> getKnownOffenders(Pageable pageable) {
        return personRepository.findByIsKnownOffender(true, pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getRiskDistribution() {
        List<Object[]> results = personRepository.countGroupedByPersonType();
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Object[] row : results) {
            Person.PersonType type = (Person.PersonType) row[0];
            Long count = (Long) row[1];
            distribution.put(type.name(), count);
        }
        return distribution;
    }

    @Transactional
    public Person createPerson(Person person) {
        Person saved = personRepository.save(person);
        log.info("Person created: id={}, name={} {}", saved.getId(), saved.getFirstName(), saved.getLastName());
        return saved;
    }

    @Transactional
    public Person updatePerson(UUID id, Person updates) {
        Person existing = getPerson(id);
        existing.setBiometricId(updates.getBiometricId());
        existing.setFirstName(updates.getFirstName());
        existing.setLastName(updates.getLastName());
        existing.setDateOfBirth(updates.getDateOfBirth());
        existing.setGender(updates.getGender());
        existing.setNationality(updates.getNationality());
        existing.setPersonType(updates.getPersonType());
        existing.setConvictionCount(updates.getConvictionCount());
        existing.setIsKnownOffender(updates.getIsKnownOffender());
        existing.setRiskScore(updates.getRiskScore());
        return personRepository.save(existing);
    }

    @Transactional
    public void deletePerson(UUID id) {
        Person existing = getPerson(id);
        personRepository.delete(existing);
        log.info("Person deleted: id={}", id);
    }
}
