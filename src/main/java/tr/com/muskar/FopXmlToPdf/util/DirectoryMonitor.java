package tr.com.muskar.FopXmlToPdf.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tr.com.muskar.FopXmlToPdf.service.FopService;

import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DirectoryMonitor implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryMonitor.class);

    @Autowired
    private FopService fopService;

    @Value("${fop.input-dir}")
    private String inputDir;

    @Override
    public void run(String... args) throws Exception {
        Path dir = Paths.get(inputDir);
        Files.createDirectories(dir);
        logger.info("Monitoring directory: {}", dir);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                logger.error("Directory monitoring interrupted", e);
                break;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path file = dir.resolve((Path) event.context());
                    if (fopService.validateFile(file)) {
                        executor.submit(() -> fopService.processFile(file));
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                logger.error("Directory watch key invalid, stopping monitoring");
                break;
            }
        }

        executor.shutdown();
    }

}
