package gov.lawenforcement.incident.service;

import gov.lawenforcement.incident.entity.Evidence;
import gov.lawenforcement.incident.repository.EvidenceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final Path uploadDir;

    public EvidenceService(EvidenceRepository evidenceRepository,
                           @Value("${app.upload.dir:uploads/evidence}") String uploadDirStr) {
        this.evidenceRepository = evidenceRepository;
        this.uploadDir = Paths.get(uploadDirStr).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public Evidence upload(Integer caseMasterId, MultipartFile file, String description, Integer uploadedBy) throws IOException {
        String ext = "";
        String origName = file.getOriginalFilename();
        if (origName != null && origName.contains(".")) {
            ext = origName.substring(origName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID() + ext;
        Path targetPath = uploadDir.resolve(storedName);
        Files.copy(file.getInputStream(), targetPath);

        Evidence evidence = new Evidence();
        evidence.setCaseMasterId(caseMasterId);
        evidence.setFileName(storedName);
        evidence.setOriginalName(origName);
        evidence.setFileType(file.getContentType());
        evidence.setFileSize(file.getSize());
        evidence.setDescription(description);
        evidence.setUploadedBy(uploadedBy);
        evidence.setUploadDate(Instant.now());
        evidence.setStoragePath(targetPath.toString());

        return evidenceRepository.save(evidence);
    }

    public List<Evidence> getByCaseId(Integer caseMasterId) {
        return evidenceRepository.findByCaseMasterId(caseMasterId);
    }

    public Resource download(Integer evidenceId) throws IOException {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found: " + evidenceId));
        Path filePath = Paths.get(evidence.getStoragePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found on disk: " + evidence.getStoragePath());
        }
        return new FileSystemResource(filePath);
    }

    public Evidence getMeta(Integer evidenceId) {
        return evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found: " + evidenceId));
    }

    public void delete(Integer evidenceId) throws IOException {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found: " + evidenceId));
        Path filePath = Paths.get(evidence.getStoragePath());
        Files.deleteIfExists(filePath);
        evidenceRepository.delete(evidence);
    }
}
