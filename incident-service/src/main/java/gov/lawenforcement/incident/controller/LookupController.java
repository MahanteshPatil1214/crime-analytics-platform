package gov.lawenforcement.incident.controller;

import gov.lawenforcement.incident.entity.*;
import gov.lawenforcement.incident.service.LookupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
@Tag(name = "Lookups", description = "Master data lookup endpoints for districts, police stations, crime heads, statuses, courts, and more")
public class LookupController {

    private final LookupService lookupService;

    @GetMapping("/states")
    @Operation(summary = "Get all states")
    public ResponseEntity<List<State>> getAllStates() {
        return ResponseEntity.ok(lookupService.getAllStates());
    }

    @GetMapping("/districts")
    @Operation(summary = "Get districts", description = "Optionally filter by state ID")
    public ResponseEntity<List<District>> getDistricts(
            @Parameter(description = "State ID to filter by") @RequestParam(required = false) Integer stateId) {
        if (stateId != null) {
            return ResponseEntity.ok(lookupService.getDistrictsByState(stateId));
        }
        return ResponseEntity.ok(lookupService.getAllDistricts());
    }

    @GetMapping("/units")
    @Operation(summary = "Get police stations", description = "Optionally filter by district ID")
    public ResponseEntity<List<Unit>> getUnits(
            @Parameter(description = "District ID to filter by") @RequestParam(required = false) Integer districtId) {
        if (districtId != null) {
            return ResponseEntity.ok(lookupService.getUnitsByDistrict(districtId));
        }
        return ResponseEntity.ok(lookupService.getAllUnits());
    }

    @GetMapping("/crime-heads")
    @Operation(summary = "Get all crime heads")
    public ResponseEntity<List<CrimeHead>> getAllCrimeHeads() {
        return ResponseEntity.ok(lookupService.getAllCrimeHeads());
    }

    @GetMapping("/crime-heads/{id}/sub-heads")
    @Operation(summary = "Get sub-heads for a crime head")
    public ResponseEntity<List<CrimeSubHead>> getSubHeadsByCrimeHead(
            @Parameter(description = "Crime head ID") @PathVariable Integer id) {
        return ResponseEntity.ok(lookupService.getSubHeadsByCrimeHead(id));
    }

    @GetMapping("/statuses")
    @Operation(summary = "Get all case statuses")
    public ResponseEntity<List<CaseStatusMaster>> getAllStatuses() {
        return ResponseEntity.ok(lookupService.getAllStatuses());
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all case categories")
    public ResponseEntity<List<CaseCategory>> getAllCategories() {
        return ResponseEntity.ok(lookupService.getAllCategories());
    }

    @GetMapping("/gravity-offences")
    @Operation(summary = "Get all gravity offences")
    public ResponseEntity<List<GravityOffence>> getAllGravityOffences() {
        return ResponseEntity.ok(lookupService.getAllGravityOffences());
    }

    @GetMapping("/courts")
    @Operation(summary = "Get all courts")
    public ResponseEntity<List<Court>> getAllCourts() {
        return ResponseEntity.ok(lookupService.getAllCourts());
    }

    @GetMapping("/employees")
    @Operation(summary = "Get all police employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(lookupService.getAllEmployees());
    }

    @GetMapping("/ranks")
    @Operation(summary = "Get all rank types")
    public ResponseEntity<List<Rank>> getAllRanks() {
        return ResponseEntity.ok(lookupService.getAllRanks());
    }

    @GetMapping("/designations")
    @Operation(summary = "Get all designations")
    public ResponseEntity<List<Designation>> getAllDesignations() {
        return ResponseEntity.ok(lookupService.getAllDesignations());
    }
}
