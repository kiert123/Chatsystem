import javax.swing.*;
import java.awt.*;
import java.io.*;

// --- UPDATED: Class name renamed from HistoryWindow to PreviousMessage ---
public class PreviousMessage {

    private JFrame frameWindow;
    private JTextArea historyTextArea;
    private JScrollPane logScrollPane;
    private String targetedLogFilePath;
    
    // New UI control component to delete old streams
    private JButton clearLogsBtn;

    // Design Color Scheme Constants
    final Color DARK_BG = new Color(30, 30, 36);      // #1E1E24
    final Color ACCENT_BG = new Color(42, 42, 53);    // #2A2A35
    final Color PRIMARY_BLUE = new Color(58, 134, 255); // #3A86FF
    final Color TEXT_WHITE = new Color(248, 249, 250); // #F8F9FA

    // --- UPDATED: Constructor renamed to match the new class name ---
    public PreviousMessage(JFrame componentOwner, String targetedLogFilePath) {
        this.targetedLogFilePath = targetedLogFilePath;
        initializeHistoryUI(componentOwner);
        loadLogDataFromFile();
    }

    private void initializeHistoryUI(JFrame componentOwner) {
        frameWindow = new JFrame("Archive Console");
        frameWindow.setSize(550, 450);
        frameWindow.setLayout(new BorderLayout());
        frameWindow.getContentPane().setBackground(DARK_BG);

        // Upper Banner Styling
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(DARK_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        JLabel windowTitleLabel = new JLabel("PERSISTENT ARCHIVE LOGS");
        windowTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        windowTitleLabel.setForeground(PRIMARY_BLUE);
        titlePanel.add(windowTitleLabel, BorderLayout.WEST);

        // Clear button component installation
        clearLogsBtn = new JButton("Clear Logs");
        clearLogsBtn.setBackground(ACCENT_BG);
        clearLogsBtn.setForeground(TEXT_WHITE);
        clearLogsBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearLogsBtn.setFocusPainted(false);
        clearLogsBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE, 1),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        titlePanel.add(clearLogsBtn, BorderLayout.EAST);

        // Archive Output Text Box Styling
        historyTextArea = new JTextArea();
        historyTextArea.setEditable(false);
        historyTextArea.setBackground(ACCENT_BG);
        historyTextArea.setForeground(TEXT_WHITE);
        
        // --- Keep Font: Uses Segoe UI Emoji to display log history emojis cleanly ---
        historyTextArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        historyTextArea.setMargin(new Insets(12, 12, 12, 12));

        logScrollPane = new JScrollPane(historyTextArea);
        logScrollPane.setBorder(BorderFactory.createLineBorder(DARK_BG, 15));

        frameWindow.add(titlePanel, BorderLayout.NORTH);
        frameWindow.add(logScrollPane, BorderLayout.CENTER);

        // Event handler hooked up to wipe data records out cleanly
        clearLogsBtn.addActionListener(e -> clearLogHistoryFile());

        frameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameWindow.setLocationRelativeTo(componentOwner);
        frameWindow.setVisible(true);
    }

    private void loadLogDataFromFile() {
        File databaseLogFile = new File(targetedLogFilePath);
        if (!databaseLogFile.exists()) {
            historyTextArea.append("// Log terminal empty. No stored history found.\n");
            return;
        }

        try (BufferedReader bufferedFileReader = new BufferedReader(new FileReader(databaseLogFile))) {
            String readBufferLine;
            while ((readBufferLine = bufferedFileReader.readLine()) != null) {
                historyTextArea.append(readBufferLine + "\n");
            }
        } catch (IOException errorEvent) {
            JOptionPane.showMessageDialog(frameWindow, "Error while reading history files.", "Error", JOptionPane.ERROR_MESSAGE);
            errorEvent.printStackTrace();
        }
    }

    private void clearLogHistoryFile() {
        int confirmDecision = JOptionPane.showConfirmDialog(
            frameWindow, 
            "Are you sure you want to permanently delete all previous message history records?", 
            "Confirm Action", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmDecision == JOptionPane.YES_OPTION) {
            // Overwriting file with empty data string clears disk tracks safely
            try (FileWriter fileWiper = new FileWriter(targetedLogFilePath, false)) {
                fileWiper.write(""); 
                historyTextArea.setText("// Log terminal empty. No stored history found.\n");
                JOptionPane.showMessageDialog(frameWindow, "Previous session logs deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException errorEvent) {
                JOptionPane.showMessageDialog(frameWindow, "Failed to erase data files safely.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- UPDATED: Calls the new PreviousMessage constructor ---
            new PreviousMessage(null, "session_logs.txt");
        });
    }
}