import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserAccount {

    private JFrame frame;
    private JTextArea accountArea;
    private final String CRED_FILE = "app_credentials.dat";
    private JButton deleteBtn;

    // Synchronized Design Color Scheme
    final Color DARK_BG = new Color(30, 30, 36);      // #1E1E24
    final Color ACCENT_BG = new Color(42, 42, 53);    // #2A2A35
    final Color PRIMARY_BLUE = new Color(58, 134, 255); // #3A86FF
    final Color TEXT_WHITE = new Color(248, 249, 250); // #F8F9FA

    public UserAccount(JFrame owner) {
        initializeUI(owner);
        loadRegisteredUsers();
    }

    private void initializeUI(JFrame owner) {
        frame = new JFrame("System Registry Console");
        frame.setSize(600, 430); 
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(DARK_BG);

        // Upper Banner Styling
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        JLabel title = new JLabel("REGISTERED SECURITY CREDENTIAL NODES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(PRIMARY_BLUE);
        topPanel.add(title, BorderLayout.WEST);

        // Delete Button Component
        deleteBtn = new JButton("Delete Account(s)");
        deleteBtn.setBackground(ACCENT_BG);
        deleteBtn.setForeground(TEXT_WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE, 1),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        topPanel.add(deleteBtn, BorderLayout.EAST);

        // Output Text Box Styling
        accountArea = new JTextArea();
        accountArea.setEditable(false);
        accountArea.setBackground(ACCENT_BG);
        accountArea.setForeground(TEXT_WHITE);
        accountArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        accountArea.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scroller = new JScrollPane(accountArea);
        scroller.setBorder(BorderFactory.createLineBorder(DARK_BG, 15));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scroller, BorderLayout.CENTER);

        // Event handler hooked up to run batch removal process
        deleteBtn.addActionListener(e -> deleteUserNodes());

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(owner);
        frame.setVisible(true);
    }

    private void loadRegisteredUsers() {
        accountArea.setText("");
        File database = new File(CRED_FILE);
        if (!database.exists()) {
            accountArea.append("// Registry database file not detected.\n");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(database))) {
            String line;
            int nodeCount = 0;
            
            accountArea.append(String.format(" %-6s   %-24s   %-20s\n", "INDEX", "VERIFIED USER NODE", "PASSCODE LINK"));
            accountArea.append("─────────────────────────────────────────────────────────────\n");

            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length >= 2) {
                    nodeCount++;
                    accountArea.append(String.format(" [%02d]     @%-24s   %-20s\n", 
                        nodeCount, 
                        credentials[0].toLowerCase(), 
                        credentials[1]
                    ));
                }
            }
            
            if (nodeCount == 0) {
                accountArea.setText("// Core registry contains zero user account entries.\n");
            }
        } catch (IOException e) {
            accountArea.setText("[ERROR] Failed to extract data segments from registry database.\n");
        }
    }

    private void deleteUserNodes() {
        File database = new File(CRED_FILE);
        if (!database.exists()) {
            JOptionPane.showMessageDialog(frame, "No registry database found to delete from.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String inputIndices = JOptionPane.showInputDialog(
            frame, 
            "Enter the INDEX number(s) to delete.\nSeparate multiple choices with commas (e.g., 1, 3, 4):", 
            "Batch Delete Node Reference", 
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (inputIndices == null || inputIndices.trim().isEmpty()) return;

        try {
            // Parse comma-separated inputs and use a Set to ignore duplicates
            Set<Integer> targets = new HashSet<>();
            String[] tokens = inputIndices.split(",");
            for (String token : tokens) {
                targets.add(Integer.parseInt(token.trim()));
            }

            List<String> retainedLines = new ArrayList<>();
            List<String> deletedUsers = new ArrayList<>();
            int currentTrackIndex = 0;
            
            // Read lines and separate matched logs out
            try (BufferedReader br = new BufferedReader(new FileReader(database))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] credentials = line.split(",");
                    if (credentials.length >= 2) {
                        currentTrackIndex++;
                        if (targets.contains(currentTrackIndex)) {
                            deletedUsers.add("@" + credentials[0]);
                            continue; // Skip adding to backup structure
                        }
                    }
                    retainedLines.add(line);
                }
            }

            if (deletedUsers.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No matching indices recognized within database tracks.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Confirm action showing what profiles will be removed
            String confirmationMessage = "Are you sure you want to permanently delete these nodes?\n" + String.join(", ", deletedUsers);
            int confirm = JOptionPane.showConfirmDialog(frame, confirmationMessage, "Confirm Batch Node Purge", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            // Rewrite app_credentials.dat with our clean list
            try (FileWriter fw = new FileWriter(database, false)) {
                for (String updatedLine : retainedLines) {
                    fw.write(updatedLine + "\n");
                }
            }

            JOptionPane.showMessageDialog(frame, "Selected targets removed cleanly from credentials file.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRegisteredUsers(); // Live panel update

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(frame, "Format Error! Please use numbers separated only by commas.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(frame, "Failed to alter storage configurations.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserAccount(null);
        });
    }
}