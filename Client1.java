import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Client1 {

    JFrame loginFrame = new JFrame("Portal Access");
    JFrame chatFrame = new JFrame("Terminal Stream");

    JTextArea chatArea = new JTextArea();
    JTextField messageInput = new JTextField();
    JButton sendBtn = new JButton("Send");

    JTextField userField = new JTextField();
    JPasswordField passField = new JPasswordField();
    JButton loginBtn = new JButton("Login");

    DataInputStream reader;
    DataOutputStream writer;
    String username;
    
    final String CRED_FILE = "app_credentials.dat";
    final String LOG_FILE = "session_logs.txt";

    // Theme Colors
    final Color CANVAS_BG = new Color(24, 24, 28);    
    final Color CARD_BG = new Color(34, 34, 44);
    final Color INPUT_BG = new Color(46, 46, 58);  
    final Color BLUE_ACCENT = new Color(58, 134, 255);  
    final Color TEXT_WHITE = new Color(248, 249, 250);  
    final Color TEXT_MUTED = new Color(173, 181, 189);  

    public Client1() {
        buildLoginUI();
    }

    public void buildLoginUI() {
        loginFrame.setSize(480, 440);
        loginFrame.setLayout(new GridBagLayout());
        loginFrame.getContentPane().setBackground(CANVAS_BG);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setPreferredSize(new Dimension(380, 320));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 65), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel title = new JLabel("SECURE PORTAL", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(BLUE_ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(2, 1, 0, 18));
        form.setBackground(CARD_BG);
        form.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        userField.setBackground(INPUT_BG); userField.setForeground(TEXT_WHITE); userField.setCaretColor(TEXT_WHITE);
        userField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_MUTED), "Username", 0, 0, null, TEXT_MUTED));
        
        passField.setBackground(INPUT_BG); passField.setForeground(TEXT_WHITE); passField.setCaretColor(TEXT_WHITE);
        passField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_MUTED), "Password", 0, 0, null, TEXT_MUTED));

        form.add(userField);
        form.add(passField);

        JPanel buttons = new JPanel(new GridLayout(1, 1, 0, 0));
        buttons.setBackground(CARD_BG);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));

        loginBtn.setBackground(BLUE_ACCENT); loginBtn.setForeground(TEXT_WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); loginBtn.setFocusPainted(false);
        buttons.add(loginBtn);

        card.add(title, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        loginFrame.add(card, new GridBagConstraints());

        loginBtn.addActionListener(e -> checkLogin());

        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    public void checkLogin() {
        username = userField.getText().trim();
        String password = new String(passField.getPassword());

        try (BufferedReader br = new BufferedReader(new FileReader(CRED_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    JOptionPane.showMessageDialog(loginFrame, "Access Granted!");
                    loginFrame.dispose();
                    connectToServer();
                    buildChatUI();
                    return;
                }
            }
        } catch (IOException e) {}
        JOptionPane.showMessageDialog(loginFrame, "Invalid Credentials.");
    }

    public void connectToServer() {
        Socket socket = null;
        try {
            UIManager.put("OptionPane.background", CANVAS_BG);
            UIManager.put("Panel.background", CANVAS_BG);
            String ip = JOptionPane.showInputDialog(chatFrame, "Enter Server IP", "localhost");
            socket = new Socket(ip, 5000);

            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
            writer.writeUTF(username);
        } catch (IOException e) {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
            JOptionPane.showMessageDialog(chatFrame, "Connection Failed!");
            System.exit(0);
        }
    }

    public void buildChatUI() {
        chatFrame.setSize(750, 580); 
        chatFrame.setLayout(new BorderLayout(10, 10));
        chatFrame.getContentPane().setBackground(CANVAS_BG);

        chatArea.setEditable(false); chatArea.setBackground(CARD_BG); chatArea.setForeground(TEXT_WHITE);
        
        // --- UPDATED: Font changed to Segoe UI Emoji so incoming stream displays emojis cleanly ---
        chatArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); 
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        
        chatArea.setLineWrap(true);       
        chatArea.setWrapStyleWord(true);  

        JScrollPane scroller = new JScrollPane(chatArea);
        scroller.setBorder(BorderFactory.createLineBorder(CANVAS_BG, 5));

        JLabel status = new JLabel("Connected session: " + username);
        status.setFont(new Font("Segoe UI", Font.BOLD, 15)); status.setForeground(TEXT_WHITE);
        JPanel topPanel = new JPanel(new BorderLayout()); topPanel.setBackground(CANVAS_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        topPanel.add(status, BorderLayout.WEST);

        // --- UPDATED: Input field also uses Segoe UI Emoji font to preview typed emojis correctly ---
        messageInput.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        messageInput.setBackground(CARD_BG); messageInput.setForeground(TEXT_WHITE); messageInput.setCaretColor(TEXT_WHITE);
        messageInput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TEXT_MUTED), BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        sendBtn.setBackground(BLUE_ACCENT); sendBtn.setForeground(TEXT_WHITE);
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); sendBtn.setFocusPainted(false);
        sendBtn.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));

        JPanel emojiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        emojiPanel.setBackground(CANVAS_BG);
        emojiPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));
        
        String[] emojiSet = {"😀", "😂", "🔥", "👍", "💯", "🚀", "⚠️", "❌"};
        for (String emoji : emojiSet) {
            JButton emoBtn = new JButton(emoji);
            emoBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            emoBtn.setBackground(CARD_BG);
            emoBtn.setForeground(TEXT_WHITE);
            emoBtn.setFocusPainted(false);
            emoBtn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            
            emoBtn.addActionListener(e -> {
                messageInput.setText(messageInput.getText() + emoji);
                messageInput.requestFocusInWindow();
            });
            emojiPanel.add(emoBtn);
        }

        JPanel interactPanel = new JPanel(new BorderLayout(5, 5));
        interactPanel.setBackground(CANVAS_BG);
        
        JPanel inputRow = new JPanel(new BorderLayout(10, 10)); 
        inputRow.setBackground(CANVAS_BG);
        inputRow.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        inputRow.add(messageInput, BorderLayout.CENTER); 
        inputRow.add(sendBtn, BorderLayout.EAST);

        interactPanel.add(emojiPanel, BorderLayout.NORTH);
        interactPanel.add(inputRow, BorderLayout.SOUTH);

        chatFrame.add(topPanel, BorderLayout.NORTH);
        chatFrame.add(scroller, BorderLayout.CENTER);
        chatFrame.add(interactPanel, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> sendMessage());
        messageInput.addActionListener(e -> sendMessage());

        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setLocationRelativeTo(null);
        chatFrame.setVisible(true);

        new Thread(() -> {
            while (true) {
                try {
                    String msg = reader.readUTF();
                    String formatted = getTime() + " " + msg;
                    chatArea.append(formatted + "\n");
                    
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                    
                    saveLog(formatted);
                } catch (IOException e) {
                    chatArea.append("Disconnected from server.\n");
                    break;
                }
            }
        }).start();
    }

    public void sendMessage() {
        try {
            String text = messageInput.getText().trim();
            if (text.isEmpty()) return;

            writer.writeUTF(text);
            String formatted = getTime() + " You: " + text;
            chatArea.append(formatted + "\n");
            
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            
            saveLog(formatted);
            messageInput.setText("");
        } catch (IOException e) {}
    }

    public void saveLog(String text) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(text + "\n");
        } catch (IOException e) {}
    }

    public String getTime() {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client1());
    }
}