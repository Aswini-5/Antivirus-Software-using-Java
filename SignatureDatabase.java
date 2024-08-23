import java.util.HashSet;
import java.util.Set;

public class SignatureDatabase {
    private final Set<String> signatures;

    public SignatureDatabase() {
        signatures = new HashSet<>();
        // Add known virus signatures
        signatures.add("virus-signature-1");
        signatures.add("virus-signature-2");
    }

    public boolean isMalicious(String fileSignature) {
        return signatures.contains(fileSignature);
    }
}
