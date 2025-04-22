package tr.com.muskar.FopXmlToPdf.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;
import tr.com.muskar.FopXmlToPdf.service.FopService;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class FopController {
    private static final Logger logger = LoggerFactory.getLogger(FopController.class);

    @Autowired
    private FopService fopService;

    @PostMapping(value = "/convert", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<FileSystemResource> convertToPdf(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        Path tempFile = Files.createTempFile("xml_", ".xml");
        try {
            file.transferTo(tempFile.toFile());
            Path pdfPath = fopService.processUploadedFile(tempFile);
            FileSystemResource resource = new FileSystemResource(pdfPath.toFile());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + pdfPath.getFileName().toString())
                    .body(resource);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
