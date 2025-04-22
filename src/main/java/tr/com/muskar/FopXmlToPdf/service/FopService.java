package tr.com.muskar.FopXmlToPdf.service;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

@Service
public class FopService {
    private static final Logger logger = LoggerFactory.getLogger(FopService.class);
    private static final long MIN_FILE_SIZE = 1024; // 1KB
    private static final Pattern INVALID_CHARS = Pattern.compile("[&%<>|]");

    private final FopFactory fopFactory;
    private final Resource xslStylesheetResource;
    private final String outputDir;

    public FopService(@Value("${fop.xsl-stylesheet}") String xslStylesheet,
                      @Value("${fop.output-dir}") String outputDir,
                      ResourceLoader resourceLoader) throws IOException {
        Resource configResource = resourceLoader.getResource("classpath:fop.xconf");
        logger.info("Loading fop.xconf from: {}", configResource.getFile().getAbsolutePath());
        if (!configResource.exists()) {
            throw new IOException("fop.xconf not found");
        }
        try {
            this.fopFactory = FopFactory.newInstance(configResource.getFile());
            this.xslStylesheetResource = resourceLoader.getResource(xslStylesheet);
            if (!this.xslStylesheetResource.exists()) {
                throw new IOException("stylesheet.xsl not found at " + xslStylesheet);
            }
            this.outputDir = outputDir;
            Files.createDirectories(Path.of(outputDir));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean validateFile(Path file) throws IOException {
        long size = Files.size(file);
        String fileName = file.getFileName().toString();
        if (!fileName.endsWith(".xml")) {
            logger.warn("Skipping {}: Not an XML file", fileName);
            return false;
        }
        if (size < MIN_FILE_SIZE) {
            logger.warn("Skipping {}: File too small ({} bytes)", fileName, size);
            return false;
        }
        if (INVALID_CHARS.matcher(fileName).find()) {
            logger.warn("Skipping {}: Invalid characters in filename", fileName);
            return false;
        }
        return true;
    }

    @Async
    public void processFile(Path xmlFile) {
        String outputPath = Path.of(outputDir, xmlFile.getFileName().toString().replace(".xml", ".pdf")).toString();
        logger.info("Processing {} to {}", xmlFile, outputPath);

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath))) {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xslStylesheetResource.getFile()));
            transformer.transform(new StreamSource(xmlFile.toFile()), new SAXResult(fop.getDefaultHandler()));
            logger.info("Successfully generated PDF: {}", outputPath);
        } catch (Exception e) {
            logger.error("Error processing {}: {}", xmlFile, e.getMessage(), e);
        }
    }

    public Path processUploadedFile(Path tempFile) throws Exception {
        if (!validateFile(tempFile)) {
            throw new IllegalArgumentException("Invalid file: " + tempFile.getFileName());
        }
        String outputPath = Path.of(outputDir, tempFile.getFileName().toString().replace(".xml", ".pdf")).toString();
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath))) {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xslStylesheetResource.getFile()));
            transformer.transform(new StreamSource(tempFile.toFile()), new SAXResult(fop.getDefaultHandler()));
            logger.info("Successfully generated PDF: {}", outputPath);
        }
        return Path.of(outputPath);
    }
}