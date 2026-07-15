import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Booking extends JFrame {

    // Theme Colors
    private final Color BG_COLOR = new Color(18, 18, 18);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(255, 193, 7); // Popcorn Yellow
    private final Color SEAT_AVAILABLE = new Color(245, 245, 247);
    private final Color SEAT_BOOKED = new Color(229, 9, 20); // Cinema Red
    private final Color SEAT_SELECTED = new Color(255, 193, 7);

    private int userId;
    private int movieId;
    private int currentShowId = 1; // Defaulting for demonstration
    private String movieName;
    
    private JLabel priceLabel;
    private List<String> selectedSeats = new ArrayList<>();
    private final double TICKET_PRICE = 12.0;

    public Booking(int userId, int movieId, String movieName) {
        this.userId = userId;
        this.movieId = movieId;
        this.movieName = movieName;

        setTitle("Book Tickets - " + movieName);
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        // --- TOP PANEL: Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BG_COLOR);
        JLabel titleLabel = new JLabel("Booking: " + movieName.toUpperCase());
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- CENTER PANEL: Showtimes & Seats ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_COLOR);

        // Showtimes Selection
        JPanel showtimePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        showtimePanel.setBackground(BG_COLOR);
        showtimePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TEXT_COLOR), "Step 1: Select Showtime"));
        ((javax.swing.border.TitledBorder) showtimePanel.getBorder()).setTitleColor(TEXT_COLOR);

        ButtonGroup timeGroup = new ButtonGroup();
        String[] times = {"10:00 AM", "02:00 PM", "06:00 PM"}; // Ideally fetched from /shows API
        for (String time : times) {
            JRadioButton rb = new JRadioButton(time);
            rb.setForeground(TEXT_COLOR);
            rb.setBackground(BG_COLOR);
            timeGroup.add(rb);
            showtimePanel.add(rb);
            if (time.equals("02:00 PM")) rb.setSelected(true); // Default selection
        }
        centerPanel.add(showtimePanel);

        // Screen Indicator
        JLabel screenLabel = new JLabel("------------------------- SCREEN -------------------------");
        screenLabel.setForeground(Color.GRAY);
        screenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(screenLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        // Seat Grid (Step 2)
        JPanel seatPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        seatPanel.setBackground(BG_COLOR);
        seatPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        String[] rows = {"A", "B", "C", "D"};
        for (String row : rows) {
            for (int i = 1; i <= 4; i++) {
                String seatNum = row + i;
                JButton seatBtn = new JButton(seatNum);
                seatBtn.setBackground(SEAT_AVAILABLE);
                seatBtn.setFocusPainted(false);
                seatBtn.setFont(new Font("Arial", Font.BOLD, 12));
                
                // Simulate some booked seats
                if (seatNum.equals("A2") || seatNum.equals("C3")) {
                    seatBtn.setBackground(SEAT_BOOKED);
                    seatBtn.setEnabled(false);
                } else {
                    seatBtn.addActionListener(new SeatSelectionListener(seatBtn, seatNum));
                }
                seatPanel.add(seatBtn);
            }
        }
        centerPanel.add(seatPanel);
        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM PANEL: Price & Booking ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        priceLabel = new JLabel("Total: $0.00");
        priceLabel.setForeground(TEXT_COLOR);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bottomPanel.add(priceLabel, BorderLayout.WEST);

        JButton bookBtn = new JButton("PROCEED TO PAYMENT");
        bookBtn.setBackground(ACCENT_COLOR);
        bookBtn.setForeground(BG_COLOR);
        bookBtn.setFont(new Font("Arial", Font.BOLD, 14));
        bookBtn.addActionListener(e -> processBooking());
        bottomPanel.add(bookBtn, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Action Listener to toggle seats
    private class SeatSelectionListener implements ActionListener {
        private JButton btn;
        private String seatNumber;

        public SeatSelectionListener(JButton btn, String seatNumber) {
            this.btn = btn;
            this.seatNumber = seatNumber;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedSeats.contains(seatNumber)) {
                selectedSeats.remove(seatNumber);
                btn.setBackground(SEAT_AVAILABLE);
            } else {
                selectedSeats.add(seatNumber);
                btn.setBackground(SEAT_SELECTED);
            }
            updatePrice();
        }
    }

    private void updatePrice() {
        double total = selectedSeats.size() * TICKET_PRICE;
        priceLabel.setText(String.format("Total: $%.2f", total));
    }

    private void processBooking() {
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one seat.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simulating the JSON payload string construction for the backend
        StringBuilder seatsArray = new StringBuilder("[");
        for (int i = 0; i < selectedSeats.size(); i++) {
            seatsArray.append("\"").append(selectedSeats.get(i)).append("\"");
            if (i < selectedSeats.size() - 1) seatsArray.append(",");
        }
        seatsArray.append("]");

        String jsonPayload = String.format("{\"user_id\": %d, \"show_id\": %d, \"seats\": %s}", 
                                            userId, currentShowId, seatsArray.toString());

        try {
            // Sending POST request to the Flask API
            URL url = new URL("http://127.0.0.1:5000/book");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Write JSON payload to output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 201) {
                JOptionPane.showMessageDialog(this, "Booking Successful!\nSeats: " + selectedSeats, "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Close window after booking
            } else {
                // Read error message from backend
                Scanner scanner = new Scanner(conn.getErrorStream());
                String response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                JOptionPane.showMessageDialog(this, "Booking Failed: \n" + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
            conn.disconnect();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection Error. Is the Flask backend running?", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Main method for testing this screen independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Booking(1, 1, "Inception").setVisible(true);
        });
    }
}
