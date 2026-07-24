package gov.lawenforcement.report.controller;

import gov.lawenforcement.report.dto.ReportRequest;
import gov.lawenforcement.report.service.ReportService;
import io.micrometer.core.annotation.Timed;
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
    @Timed(value = "report.fir", description = "Time to generate FIR report")
    public ResponseEntity<byte[]> generateFirReport(@PathVariable Integer caseId) {
        log.info("Received FIR report request for caseId: {}", caseId);
        byte[] pdfBytes = reportService.generateFirReport(caseId);
        return buildPdfResponse(pdfBytes, "FIR_Report_" + caseId);
    }

    @PostMapping("/incident")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Timed(value = "report.incident", description = "Time to generate incident report")
    public ResponseEntity<byte[]> generateIncidentReport(@RequestBody ReportRequest request) {
        log.info("Received incident report request for FIR: {}", request.getFirNumber());
        byte[] pdfBytes = reportService.generateIncidentReport(
                request.getFirNumber(), request.getTitle(), request.getDescription(),
                request.getSeverity(), request.getStatus(), request.getDistrict(),
                request.getDate(), request.getAddress()
        );
        return buildPdfResponse(pdfBytes, "Incident_Report_" + request.getFirNumber());
    }

    @PostMapping("/criminal-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Timed(value = "report.criminalProfile", description = "Time to generate criminal profile")
    public ResponseEntity<byte[]> generateCriminalProfile(@RequestBody ReportRequest request) {
        log.info("Received criminal profile request for: {}", request.getPersonName());
        byte[] pdfBytes = reportService.generateCriminalProfile(
                request.getPersonName(), request.getPersonType(),
                request.getConvictionCount(), request.getRiskScore(), request.getCharges()
        );
        return buildPdfResponse(pdfBytes, "Criminal_Profile_" + request.getPersonName().replaceAll("\\s+", "_"));
    }

    private ResponseEntity<byte[]> buildPdfResponse(byte[] pdfBytes, String baseName) {
        String filename = baseName + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
