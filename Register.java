import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passwordField;

    public Register() {
        setTitle("Movie Hub - Register");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        namePanel.add(nameField);

        JPanel emailPanel = new JPanel();
        emailPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        emailPanel.add(emailField);

        JPanel passPanel = new JPanel();
        passPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        passPanel.add(passwordField);

        JButton registerBtn = new JButton("SIGN UP");
        registerBtn.addActionListener(e -> registerUser());

        JButton backBtn = new JButton("Back to Login");
        backBtn.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        add(new JLabel("🎬 CREATE ACCOUNT", SwingConstants.CENTER));
        add(namePanel);
        add(emailPanel);
        add(passPanel);
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);
        add(btnPanel);
    }

    private void registerUser() {
        try {
            URL url = new URL("http://127.0.0.1:5000/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String jsonInput = String.format("{\"name\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}", name, email, password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() == 201) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                new Login().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}