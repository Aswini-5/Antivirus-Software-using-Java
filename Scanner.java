import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Scanner {
    private final SignatureDatabase signatureDatabase;
    private final AntivirusUI ui;

    public Scanner(SignatureDatabase signatureDatabase, AntivirusUI ui) {
        this.signatureDatabase = signatureDatabase;
        this.ui = ui;
    }

    public boolean scanFile(File file) throws IOException, NoSuchAlgorithmException {
        String fileSignature = getFileSignature(file);
        boolean isMalicious = signatureDatabase.isMalicious(fileSignature);
        ui.logMessage("Scanning file: " + file.getAbsolutePath());
        if (isMalicious) {
            ui.logMessage("Malicious file detected: " + file.getAbsolutePath());
        } else {
            ui.logMessage("File is clean: " + file.getAbsolutePath());
        }
        return isMalicious;
    }

    public void scanDirectory(File directory) throws IOException, NoSuchAlgorithmException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file); // Recursive call
                } else {
                    scanFile(file); // Scan each file
                }
            }
        }
    }

    private String getFileSignature(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] dataBytes = new byte[1024];
            int nRead;
            while ((nRead = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nRead);
            }
        }
        byte[] mdBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : mdBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
