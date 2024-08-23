import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class Antivirus {
    public static void main(String[] args) {
        try {
            SignatureDatabase signatureDatabase = new SignatureDatabase();
            Scanner scanner = new Scanner(signatureDatabase);

            // Example of on-demand scan
            File fileToScan = new File("path/to/file.txt");
            if (scanner.scanFile(fileToScan)) {
                System.out.println("Malicious file detected: " + fileToScan.getAbsolutePath());
            } else {
                System.out.println("File is clean: " + fileToScan.getAbsolutePath());
            }

            // Real-time monitoring
            Path directoryToMonitor = Paths.get("path/to/directory");
            RealTimeMonitor monitor = new RealTimeMonitor(directoryToMonitor, scanner);
            new Thread(() -> {
                try {
                    monitor.startMonitoring();
                } catch (IOException e) {
                }
            }).start();

        } catch (IOException | NoSuchAlgorithmException e) {
        }
    }
}
