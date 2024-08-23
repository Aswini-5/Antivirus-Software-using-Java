import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.NoSuchAlgorithmException;

public class RealTimeMonitor {
    private final Path path;
    private final Scanner scanner;

    public RealTimeMonitor(Path path, Scanner scanner) {
        this.path = path;
        this.scanner = scanner;
    }

    public void startMonitoring() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        
        WatchKey key;
        while ((key = watchService.poll()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                Path filePath = (Path) event.context();
                File file = path.resolve(filePath).toFile();
                try {
                    if (scanner.scanFile(file)) {
                        System.out.println("Malicious file detected: " + file.getAbsolutePath());
                    }
                } catch (IOException | NoSuchAlgorithmException e) {
                }
            }
            key.reset();
        }
    }
}
