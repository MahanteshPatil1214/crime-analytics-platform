package gov.lawenforcement.incident.controller;

import gov.lawenforcement.incident.entity.Evidence;
import gov.lawenforcement.incident.service.EvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cases/{caseId}/evidence")
@Tag(name = "Evidence", description = "File upload, download, and management for case evidence (FIR scans, photos, forensic reports)")
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload evidence file", description = "Upload a file (PDF, image, document) attached to a case")
    public ResponseEntity<Evidence> upload(
            @Parameter(description = "Case master ID") @PathVariable Integer caseId,
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Optional description") @RequestParam(required = false) String description,
            @Parameter(description = "Uploader employee ID") @RequestParam(required = false) Integer uploadedBy) throws IOException {
        Evidence evidence = evidenceService.upload(caseId, file, description, uploadedBy);
        return ResponseEntity.ok(evidence);
    }

    @GetMapping
    @Operation(summary = "List evidence for a case")
    public ResponseEntity<List<Evidence>> list(@Parameter(description = "Case master ID") @PathVariable Integer caseId) {
        return ResponseEntity.ok(evidenceService.getByCaseId(caseId));
    }

    @GetMapping("/{evidenceId}/download")
    @Operation(summary = "Download evidence file")
    public ResponseEntity<Resource> download(
            @Parameter(description = "Case master ID") @PathVariable Integer caseId,
            @Parameter(description = "Evidence ID") @PathVariable Integer evidenceId) throws IOException {
        Resource resource = evidenceService.download(evidenceId);
        Evidence meta = evidenceService.getMeta(evidenceId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getOriginalName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{evidenceId}")
    @Operation(summary = "Delete evidence file")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Case master ID") @PathVariable Integer caseId,
            @Parameter(description = "Evidence ID") @PathVariable Integer evidenceId) throws IOException {
        evidenceService.delete(evidenceId);
        return ResponseEntity.noContent().build();
    }
}
