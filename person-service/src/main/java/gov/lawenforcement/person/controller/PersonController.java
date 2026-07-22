package gov.lawenforcement.person.controller;

import gov.lawenforcement.person.entity.Person;
import gov.lawenforcement.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable UUID id) {
        return ResponseEntity.ok(personService.getPerson(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Person>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String personType,
            @RequestParam(required = false) Double minRisk,
            @RequestParam(required = false) Double maxRisk,
            Pageable pageable) {
        Person.PersonType pt = null;
        if (personType != null && !personType.isBlank()) {
            pt = Person.PersonType.valueOf(personType.toUpperCase());
        }
        return ResponseEntity.ok(personService.searchPersons(name, pt, minRisk, maxRisk, pageable));
    }

    @GetMapping("/offenders")
    public ResponseEntity<Page<Person>> getKnownOffenders(Pageable pageable) {
        return ResponseEntity.ok(personService.getKnownOffenders(pageable));
    }

    @GetMapping("/stats/risk-distribution")
    public ResponseEntity<Map<String, Long>> getRiskDistribution() {
        return ResponseEntity.ok(personService.getRiskDistribution());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        return ResponseEntity.ok(personService.createPerson(person));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<Person> updatePerson(@PathVariable UUID id, @RequestBody Person person) {
        return ResponseEntity.ok(personService.updatePerson(id, person));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}
