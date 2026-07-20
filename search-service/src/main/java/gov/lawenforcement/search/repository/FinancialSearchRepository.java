package gov.lawenforcement.search.repository;

import gov.lawenforcement.search.document.FinancialDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FinancialSearchRepository extends ElasticsearchRepository<FinancialDocument, String> {
}
