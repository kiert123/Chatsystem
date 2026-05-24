import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Register {

    private JFrame frame;
    private JTextField userField;
    private JPasswordField passField;
    private JPasswordField confirmField;
    private JButton registerBtn;
    private JButton cancelBtn;

    // Design Color Scheme Constants (Matches HistoryWindow)
    final Color DARK_BG = new Color(30, 30, 36);      // #1E1E24
    final Color ACCENT_BG = new Color(42, 42, 53);    // #2A2A35
    final Color PRIMARY_BLUE = new Color(58, 134, 255); // #3A86FF
    final Color TEXT_WHITE = new Color(248, 249, 250); // #F8F9FA
    final Color TEXT_MUTED = new Color(173, 181, 189); // #ADB5BD

    public Register() {
        initializeRegisterUI();
    }

    private void initializeRegisterUI() {
        frame = new JFrame("Account Creation Portal");
        frame.setSize(480, 480);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(DARK_BG);

        // Main Central Card Container
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ACCENT_BG);
        card.setPreferredSize(new Dimension(380, 380));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 55, 68), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Window Title Header
        JLabel titleLabel = new JLabel("CREATE NEW USER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_BLUE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Form Fields Layout Container
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 18));
        formPanel.setBackground(ACCENT_BG);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        userField = new JTextField();
        passField = new JPasswordField();
        confirmField = new JPasswordField();

        setupFieldStyle(userField, "New Username");
        setupFieldStyle(passField, "New Password");
        setupFieldStyle(confirmField, "Confirm Password");

        formPanel.add(userField);
        formPanel.add(passField);
        formPanel.add(confirmField);

        // Control Buttons Layout
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setBackground(ACCENT_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));

        registerBtn = new JButton("Register User");
        registerBtn.setBackground(PRIMARY_BLUE);
        registerBtn.setForeground(TEXT_WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);

        cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(DARK_BG);
        cancelBtn.setForeground(TEXT_WHITE);
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));

        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(card, new GridBagConstraints());

        // Event Triggers
        registerBtn.addActionListener(e -> processRegistration());
        cancelBtn.addActionListener(e -> frame.dispose());

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupFieldStyle(JTextField field, String placeholderText) {
        field.setBackground(DARK_BG);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(TEXT_WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_MUTED, 1), 
            placeholderText, 0, 0, null, TEXT_MUTED
        ));
    }

    private void processRegistration() {
        String inputUser = userField.getText().trim();
        String inputPass = new String(passField.getPassword());
        String inputConfirm = new String(confirmField.getPassword());

        if (inputUser.isEmpty() || inputPass.isEmpty() || inputConfirm.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please complete all fields.", "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!inputPass.equals(inputConfirm)) {
            JOptionPane.showMessageDialog(frame, "Passwords do not match!", "Match Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (FileWriter dataWriter = new FileWriter("app_credentials.dat", true)) {
            dataWriter.write(inputUser + "," + inputPass + "\n");
            JOptionPane.showMessageDialog(frame, "User successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose(); // Closes the window automatically
        } catch (IOException error) {
            JOptionPane.showMessageDialog(frame, "Failed to update database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Direct Execution Point: Runs Register window completely separate from Server
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Register();
        });
    }
}