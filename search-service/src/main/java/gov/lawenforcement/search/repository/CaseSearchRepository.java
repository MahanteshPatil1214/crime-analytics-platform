package gov.lawenforcement.search.repository;

import gov.lawenforcement.search.document.CaseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CaseSearchRepository extends ElasticsearchRepository<CaseDocument, String> {
    List<CaseDocument> findByDistrictName(String district);
    List<CaseDocument> findByStatusName(String status);
    List<CaseDocument> findByCrimeHeadName(String crimeHead);
}
