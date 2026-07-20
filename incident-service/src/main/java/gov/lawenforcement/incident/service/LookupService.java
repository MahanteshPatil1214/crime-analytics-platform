package gov.lawenforcement.incident.service;

import gov.lawenforcement.incident.entity.*;
import gov.lawenforcement.incident.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LookupService {

    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final UnitRepository unitRepository;
    private final CrimeHeadRepository crimeHeadRepository;
    private final CrimeSubHeadRepository crimeSubHeadRepository;
    private final CaseStatusMasterRepository caseStatusMasterRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final GravityOffenceRepository gravityOffenceRepository;
    private final CourtRepository courtRepository;
    private final EmployeeRepository employeeRepository;
    private final RankRepository rankRepository;
    private final DesignationRepository designationRepository;

    public List<State> getAllStates() {
        return stateRepository.findAll();
    }

    public List<District> getAllDistricts() {
        return districtRepository.findAll();
    }

    public List<District> getDistrictsByState(Integer stateId) {
        return districtRepository.findByStateId(stateId);
    }

    public List<Unit> getUnitsByDistrict(Integer districtId) {
        return unitRepository.findByDistrictId(districtId);
    }

    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    public List<CrimeHead> getAllCrimeHeads() {
        return crimeHeadRepository.findAll();
    }

    public List<CrimeSubHead> getSubHeadsByCrimeHead(Integer crimeHeadId) {
        return crimeSubHeadRepository.findByCrimeHeadId(crimeHeadId);
    }

    public List<CaseStatusMaster> getAllStatuses() {
        return caseStatusMasterRepository.findAll();
    }

    public List<CaseCategory> getAllCategories() {
        return caseCategoryRepository.findAll();
    }

    public List<GravityOffence> getAllGravityOffences() {
        return gravityOffenceRepository.findAll();
    }

    public List<Court> getAllCourts() {
        return courtRepository.findAll();
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Rank> getAllRanks() {
        return rankRepository.findAll();
    }

    public List<Designation> getAllDesignations() {
        return designationRepository.findAll();
    }
}
