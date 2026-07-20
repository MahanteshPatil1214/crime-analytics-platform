package gov.lawenforcement.incident.controller;

import gov.lawenforcement.incident.entity.*;
import gov.lawenforcement.incident.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    @GetMapping("/states")
    public ResponseEntity<List<State>> getAllStates() {
        return ResponseEntity.ok(lookupService.getAllStates());
    }

    @GetMapping("/districts")
    public ResponseEntity<List<District>> getDistricts(
            @RequestParam(required = false) Integer stateId) {
        if (stateId != null) {
            return ResponseEntity.ok(lookupService.getDistrictsByState(stateId));
        }
        return ResponseEntity.ok(lookupService.getAllDistricts());
    }

    @GetMapping("/units")
    public ResponseEntity<List<Unit>> getUnits(
            @RequestParam(required = false) Integer districtId) {
        if (districtId != null) {
            return ResponseEntity.ok(lookupService.getUnitsByDistrict(districtId));
        }
        return ResponseEntity.ok(lookupService.getAllUnits());
    }

    @GetMapping("/crime-heads")
    public ResponseEntity<List<CrimeHead>> getAllCrimeHeads() {
        return ResponseEntity.ok(lookupService.getAllCrimeHeads());
    }

    @GetMapping("/crime-heads/{id}/sub-heads")
    public ResponseEntity<List<CrimeSubHead>> getSubHeadsByCrimeHead(@PathVariable Integer id) {
        return ResponseEntity.ok(lookupService.getSubHeadsByCrimeHead(id));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<CaseStatusMaster>> getAllStatuses() {
        return ResponseEntity.ok(lookupService.getAllStatuses());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CaseCategory>> getAllCategories() {
        return ResponseEntity.ok(lookupService.getAllCategories());
    }

    @GetMapping("/gravity-offences")
    public ResponseEntity<List<GravityOffence>> getAllGravityOffences() {
        return ResponseEntity.ok(lookupService.getAllGravityOffences());
    }

    @GetMapping("/courts")
    public ResponseEntity<List<Court>> getAllCourts() {
        return ResponseEntity.ok(lookupService.getAllCourts());
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(lookupService.getAllEmployees());
    }

    @GetMapping("/ranks")
    public ResponseEntity<List<Rank>> getAllRanks() {
        return ResponseEntity.ok(lookupService.getAllRanks());
    }

    @GetMapping("/designations")
    public ResponseEntity<List<Designation>> getAllDesignations() {
        return ResponseEntity.ok(lookupService.getAllDesignations());
    }
}
