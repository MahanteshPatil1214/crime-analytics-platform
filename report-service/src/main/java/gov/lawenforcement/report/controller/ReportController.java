package gov.lawenforcement.report.controller;

import gov.lawenforcement.report.dto.ReportRequest;
import gov.lawenforcement.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/fir/{caseId}")
    public ResponseEntity<byte[]> generateFirReport(@PathVariable Integer caseId) {
        log.info("Received FIR report request for caseId: {}", caseId);

        try {
            byte[] pdfBytes = reportService.generateFirReport(caseId);

            String filename = "FIR_Report_" + caseId + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            log.error("Failed to generate FIR report for caseId: {}", caseId, e);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(("{\"error\": \"PDF generation failed: " + e.getMessage() + "\"}").getBytes());
        }
    }

    @PostMapping("/incident")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<byte[]> generateIncidentReport(@RequestBody ReportRequest request) {
        log.info("Received incident report request for FIR: {}", request.getFirNumber());

        try {
            byte[] pdfBytes = reportService.generateIncidentReport(
                    request.getFirNumber(),
                    request.getTitle(),
                    request.getDescription(),
                    request.getSeverity(),
                    request.getStatus(),
                    request.getDistrict(),
                    request.getDate(),
                    request.getAddress()
            );

            String filename = "Incident_Report_" + request.getFirNumber() + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            log.error("Failed to generate incident report for FIR: {}", request.getFirNumber(), e);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(("{\"error\": \"PDF generation failed: " + e.getMessage() + "\"}").getBytes());
        }
    }

    @PostMapping("/criminal-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<byte[]> generateCriminalProfile(@RequestBody ReportRequest request) {
        log.info("Received criminal profile request for: {}", request.getPersonName());

        try {
            byte[] pdfBytes = reportService.generateCriminalProfile(
                    request.getPersonName(),
                    request.getPersonType(),
                    request.getConvictionCount(),
                    request.getRiskScore(),
                    request.getCharges()
            );

            String filename = "Criminal_Profile_" + request.getPersonName().replaceAll("\\s+", "_") + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            log.error("Failed to generate criminal profile for: {}", request.getPersonName(), e);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(("{\"error\": \"PDF generation failed: " + e.getMessage() + "\"}").getBytes());
        }
    }
}
