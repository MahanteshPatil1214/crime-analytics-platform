package gov.lawenforcement.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import gov.lawenforcement.search.document.CaseDocument;
import gov.lawenforcement.search.document.FinancialDocument;
import gov.lawenforcement.search.document.PersonDocument;
import gov.lawenforcement.search.repository.CaseSearchRepository;
import gov.lawenforcement.search.repository.FinancialSearchRepository;
import gov.lawenforcement.search.repository.PersonSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final CaseSearchRepository caseRepo;
    private final PersonSearchRepository personRepo;
    private final FinancialSearchRepository financialRepo;
    private final ElasticsearchOperations esOps;

    public Map<String, Object> searchCases(String query, String district, String status, String crimeHead) {
        if ((query == null || query.isBlank()) && (district == null || district.isBlank())
                && (status == null || status.isBlank()) && (crimeHead == null || crimeHead.isBlank())) {
            List<CaseDocument> all = new ArrayList<>();
            caseRepo.findAll().forEach(all::add);
            return Map.of("results", all, "total", all.size());
        }

        Query q = NativeQuery.builder().withQuery(nq -> nq.bool(b -> {
            if (query != null && !query.isBlank()) {
                b.must(m -> m.multiMatch(mm -> mm
                    .query(query)
                    .fields("briefFacts^2", "accusedNames^3", "victimNames^2", "complainantNames^2", "crimeNo^4", "searchableText")));
            }
            if (district != null && !district.isBlank()) {
                b.filter(f -> f.term(t -> t.field("districtName").value(district)));
            }
            if (status != null && !status.isBlank()) {
                b.filter(f -> f.term(t -> t.field("statusName").value(status)));
            }
            if (crimeHead != null && !crimeHead.isBlank()) {
                b.filter(f -> f.term(t -> t.field("crimeHeadName").value(crimeHead)));
            }
            return b;
        })).withMaxResults(100).build();

        SearchHits<CaseDocument> hits = esOps.search(q, CaseDocument.class);
        List<CaseDocument> results = hits.getSearchHits().stream().map(h -> h.getContent()).collect(Collectors.toList());
        return Map.of("results", results, "total", hits.getTotalHits());
    }

    public Map<String, Object> searchPersons(String query, String type) {
        if ((query == null || query.isBlank()) && (type == null || type.isBlank())) {
            List<PersonDocument> all = new ArrayList<>();
            personRepo.findAll().forEach(all::add);
            return Map.of("results", all, "total", all.size());
        }

        Query q = NativeQuery.builder().withQuery(nq -> nq.bool(b -> {
            if (query != null && !query.isBlank()) {
                b.must(m -> m.multiMatch(mm -> mm.query(query).fields("name^3", "caseCrimeNos")));
            }
            if (type != null && !type.isBlank()) {
                b.filter(f -> f.term(t -> t.field("personType").value(type.toUpperCase())));
            }
            return b;
        })).withMaxResults(100).build();

        SearchHits<PersonDocument> hits = esOps.search(q, PersonDocument.class);
        List<PersonDocument> results = hits.getSearchHits().stream().map(h -> h.getContent()).collect(Collectors.toList());
        return Map.of("results", results, "total", hits.getTotalHits());
    }

    public Map<String, Object> searchFinancial(String query, Boolean flagged) {
        if ((query == null || query.isBlank()) && (flagged == null || !flagged)) {
            List<FinancialDocument> all = new ArrayList<>();
            financialRepo.findAll().forEach(all::add);
            return Map.of("results", all, "total", all.size());
        }

        Query q = NativeQuery.builder().withQuery(nq -> nq.bool(b -> {
            if (query != null && !query.isBlank()) {
                b.must(m -> m.multiMatch(mm -> mm.query(query).fields("transactionRef^3", "senderAccountId", "recipientAccountId", "relatedCaseCrimeNo^2")));
            }
            if (flagged != null && flagged) {
                b.filter(f -> f.term(t -> t.field("flagged").value(true)));
            }
            return b;
        })).withMaxResults(100).build();

        SearchHits<FinancialDocument> hits = esOps.search(q, FinancialDocument.class);
        List<FinancialDocument> results = hits.getSearchHits().stream().map(h -> h.getContent()).collect(Collectors.toList());
        return Map.of("results", results, "total", hits.getTotalHits());
    }

    public Map<String, Object> globalSearch(String query) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cases", searchCases(query, null, null, null));
        result.put("persons", searchPersons(query, null));
        result.put("financial", searchFinancial(query, null));
        return result;
    }

    public List<Map<String, String>> autocomplete(String prefix) {
        if (prefix == null || prefix.isBlank()) return List.of();

        Query q = NativeQuery.builder().withQuery(nq -> nq.bool(b -> b
            .should(s -> s.multiMatch(m -> m.query(prefix).fields("name^5", "crimeNo^3", "transactionRef^2").fuzziness("AUTO")))))
            .withMaxResults(10).build();

        SearchHits<PersonDocument> hits = esOps.search(q, PersonDocument.class);
        return hits.getSearchHits().stream().map(h -> {
            PersonDocument p = h.getContent();
            Map<String, String> item = new LinkedHashMap<>();
            item.put("text", p.getName());
            item.put("type", p.getPersonType());
            item.put("id", p.getId());
            return item;
        }).collect(Collectors.toList());
    }
}
