import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Movie Hub - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10));

        JPanel emailPanel = new JPanel();
        emailPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        emailPanel.add(emailField);

        JPanel passPanel = new JPanel();
        passPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        passPanel.add(passwordField);

        JButton loginButton = new JButton("LOGIN");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });

        JButton registerButton = new JButton("Don't have an account? Sign Up");
        registerButton.addActionListener(e -> {
            new Register().setVisible(true);
            dispose();
        });

        add(new JLabel("🎬 MOVIE BOOKING SYSTEM", SwingConstants.CENTER));
        add(emailPanel);
        add(passPanel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel);
    }

    private void authenticateUser() {
        try {
            URL url = new URL("http://127.0.0.1:5000/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String jsonInputString = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                int userId = parseUserId(response.toString());
                if (userId > 0) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    new Movies(userId).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Login failed to retrieve user ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Server connection failed.");
        }
    }

    private int parseUserId(String json) {
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"user_id\"\\s*:\\s*(\\d+)");
            java.util.regex.Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}