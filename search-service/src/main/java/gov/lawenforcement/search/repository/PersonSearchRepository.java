package gov.lawenforcement.search.repository;

import gov.lawenforcement.search.document.PersonDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PersonSearchRepository extends ElasticsearchRepository<PersonDocument, String> {
    List<PersonDocument> findByPersonType(String personType);
}
