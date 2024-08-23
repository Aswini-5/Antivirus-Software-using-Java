import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class AntivirusUI {
    private final JFrame frame;
    private final JTextArea resultArea;
    private final SignatureDatabase signatureDatabase;
    private final Scanner scanner;
    private RealTimeMonitor monitor;

    public AntivirusUI() {
        signatureDatabase = new SignatureDatabase();
        scanner = new Scanner(signatureDatabase, this);

        frame = new JFrame("Antivirus Software");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton scanFileButton = new JButton("Scan File");
        scanFileButton.addActionListener(new ScanFileButtonListener());
        panel.add(scanFileButton);

        JButton scanAllButton = new JButton("Scan All Files");
        scanAllButton.addActionListener(new ScanAllButtonListener());
        panel.add(scanAllButton);

        JButton monitorButton = new JButton("Start Monitoring");
        monitorButton.addActionListener(new MonitorButtonListener());
        panel.add(monitorButton);

        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    public void logMessage(String message) {
        SwingUtilities.invokeLater(() -> resultArea.append(message + "\n"));
    }

    private class ScanFileButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    logMessage("Scanning file: " + file.getAbsolutePath());
                    boolean isMalicious = scanner.scanFile(file);
                    if (isMalicious) {
                        logMessage("Malicious file detected: " + file.getAbsolutePath());
                    } else {
                        logMessage("File is clean: " + file.getAbsolutePath());
                    }
                } catch (IOException | NoSuchAlgorithmException ex) {
                    logMessage("Error scanning file: " + ex.getMessage());
                }
            }
        }
    }

    private class ScanAllButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File directory = fileChooser.getSelectedFile();
                new Thread(() -> {
                    try {
                        logMessage("Starting full scan of directory: " + directory.getAbsolutePath());
                        scanner.scanDirectory(directory);
                        logMessage("Scan completed.");
                    } catch (IOException | NoSuchAlgorithmException ex) {
                        logMessage("Error scanning directory: " + ex.getMessage());
                    }
                }).start();
            }
        }
    }

    private class MonitorButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                Path directoryToMonitor = fileChooser.getSelectedFile().toPath();
                monitor = new RealTimeMonitor(directoryToMonitor, scanner);
                new Thread(() -> {
                    try {
                        logMessage("Monitoring started on directory: " + directoryToMonitor.toString());
                        monitor.startMonitoring();
                    } catch (IOException ex) {
                        logMessage("Error starting monitoring: " + ex.getMessage());
                    }
                }).start();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AntivirusUI::new);
    }
}
