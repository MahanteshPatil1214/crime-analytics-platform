package gov.lawenforcement.incident.service;

import gov.lawenforcement.incident.entity.Evidence;
import gov.lawenforcement.incident.repository.EvidenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvidenceServiceTest {

    @Mock
    private EvidenceRepository evidenceRepository;

    @Captor
    private ArgumentCaptor<Evidence> evidenceCaptor;

    private EvidenceService evidenceService;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        evidenceService = new EvidenceService(evidenceRepository, tempDir.toString());
        evidenceService.init();
    }

    @Test
    void upload_savesEvidenceAndFile(@TempDir Path tempDir) throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "crime-scene-photo.jpg", "image/jpeg", "fake-image-data".getBytes());

        Evidence savedEvidence = new Evidence();
        savedEvidence.setEvidenceId(1);
        savedEvidence.setFileName("stored-uuid.jpg");
        savedEvidence.setStoragePath(tempDir.resolve("stored-uuid.jpg").toString());

        when(evidenceRepository.save(any())).thenReturn(savedEvidence);

        Evidence result = evidenceService.upload(123, file, "Crime scene photo", 456);

        assertNotNull(result);
        verify(evidenceRepository).save(evidenceCaptor.capture());
        Evidence captured = evidenceCaptor.getValue();

        assertEquals(123, captured.getCaseMasterId());
        assertEquals("crime-scene-photo.jpg", captured.getOriginalName());
        assertEquals("image/jpeg", captured.getFileType());
        assertEquals(15L, captured.getFileSize());
        assertEquals("Crime scene photo", captured.getDescription());
        assertEquals(456, captured.getUploadedBy());
        assertNotNull(captured.getUploadDate());
        assertNotNull(captured.getStoragePath());
        assertNotNull(captured.getFileName());
        assertTrue(captured.getFileName().endsWith(".jpg"));
    }

    @Test
    void upload_fileWithoutExtension(@TempDir Path tempDir) throws IOException {
        evidenceService = new EvidenceService(evidenceRepository, tempDir.toString());
        evidenceService.init();

        MockMultipartFile file = new MockMultipartFile(
                "file", "noext", "application/octet-stream", "data".getBytes());

        Evidence savedEvidence = new Evidence();
        savedEvidence.setEvidenceId(2);
        when(evidenceRepository.save(any())).thenReturn(savedEvidence);

        Evidence result = evidenceService.upload(1, file, null, null);

        assertNotNull(result);
        verify(evidenceRepository).save(evidenceCaptor.capture());
        assertFalse(evidenceCaptor.getValue().getFileName().contains("."));
    }

    @Test
    void getByCaseId_returnsEvidenceList() {
        Evidence e1 = new Evidence();
        e1.setEvidenceId(1);
        Evidence e2 = new Evidence();
        e2.setEvidenceId(2);
        when(evidenceRepository.findByCaseMasterId(10)).thenReturn(List.of(e1, e2));

        List<Evidence> result = evidenceService.getByCaseId(10);

        assertEquals(2, result.size());
        verify(evidenceRepository).findByCaseMasterId(10);
    }

    @Test
    void download_fileExists_returnsResource(@TempDir Path tempDir) throws IOException {
        evidenceService = new EvidenceService(evidenceRepository, tempDir.toString());
        evidenceService.init();

        Path testFile = tempDir.resolve("test-file.txt");
        Files.writeString(testFile, "evidence content");

        Evidence evidence = new Evidence();
        evidence.setEvidenceId(1);
        evidence.setStoragePath(testFile.toString());

        when(evidenceRepository.findById(1)).thenReturn(Optional.of(evidence));

        Resource resource = evidenceService.download(1);

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void download_fileNotFound_throwsException(@TempDir Path tempDir) throws IOException {
        evidenceService = new EvidenceService(evidenceRepository, tempDir.toString());
        evidenceService.init();

        Evidence evidence = new Evidence();
        evidence.setEvidenceId(1);
        evidence.setStoragePath(tempDir.resolve("nonexistent.pdf").toString());

        when(evidenceRepository.findById(1)).thenReturn(Optional.of(evidence));

        assertThrows(RuntimeException.class, () -> evidenceService.download(1));
    }

    @Test
    void download_evidenceNotFound_throwsException() {
        when(evidenceRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> evidenceService.download(999));
    }

    @Test
    void getMeta_evidenceFound() {
        Evidence evidence = new Evidence();
        evidence.setEvidenceId(1);
        evidence.setOriginalName("report.pdf");
        when(evidenceRepository.findById(1)).thenReturn(Optional.of(evidence));

        Evidence result = evidenceService.getMeta(1);

        assertEquals("report.pdf", result.getOriginalName());
    }

    @Test
    void getMeta_notFound_throwsException() {
        when(evidenceRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> evidenceService.getMeta(999));
    }

    @Test
    void delete_deletesFileAndRecord(@TempDir Path tempDir) throws IOException {
        evidenceService = new EvidenceService(evidenceRepository, tempDir.toString());
        evidenceService.init();

        Path testFile = tempDir.resolve("to-delete.txt");
        Files.createFile(testFile);

        Evidence evidence = new Evidence();
        evidence.setEvidenceId(1);
        evidence.setStoragePath(testFile.toString());

        when(evidenceRepository.findById(1)).thenReturn(Optional.of(evidence));

        evidenceService.delete(1);

        assertFalse(Files.exists(testFile));
        verify(evidenceRepository).delete(evidence);
    }

    @Test
    void delete_evidenceNotFound_throwsException() {
        when(evidenceRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> evidenceService.delete(999));
    }
}
