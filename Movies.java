import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Movies extends JFrame {
    private int userId;

    public Movies(int userId) {
        this.userId = userId;
        setTitle("Available Movies");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("🎬 Select a Movie to Book", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Panel to hold our dynamic movie cards
        JPanel moviePanel = new JPanel();
        moviePanel.setLayout(new BoxLayout(moviePanel, BoxLayout.Y_AXIS));
        moviePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Fetch and parse the data
        String jsonResponse = fetchMovies();
        parseAndDisplayMovies(jsonResponse, moviePanel);

        // Add a scroll pane in case the movie list gets long
        JScrollPane scrollPane = new JScrollPane(moviePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private String fetchMovies() {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("http://127.0.0.1:5000/movies");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return response.toString();
    }

    private void parseAndDisplayMovies(String json, JPanel panel) {
        if (json == null || json.isEmpty()) {
            panel.add(new JLabel("No movies available or server is down."));
            return;
        }

        // Isolate each JSON object {} inside the array
        Matcher objMatcher = Pattern.compile("\\{(.*?)\\}").matcher(json);
        
        while (objMatcher.find()) {
            String movieObj = objMatcher.group(1);
            
            int movieId = -1;
            String movieName = "Unknown";
            String genre = "Unknown";
            String duration = "Unknown";
            
            // Extract individual fields using Regex
            Matcher idMatcher = Pattern.compile("\"movie_id\"\\s*:\\s*(\\d+)").matcher(movieObj);
            if (idMatcher.find()) movieId = Integer.parseInt(idMatcher.group(1));
            
            Matcher nameMatcher = Pattern.compile("\"movie_name\"\\s*:\\s*\"([^\"]+)\"").matcher(movieObj);
            if (nameMatcher.find()) movieName = nameMatcher.group(1);

            Matcher genreMatcher = Pattern.compile("\"genre\"\\s*:\\s*\"([^\"]+)\"").matcher(movieObj);
            if (genreMatcher.find()) genre = genreMatcher.group(1);

            Matcher durationMatcher = Pattern.compile("\"duration\"\\s*:\\s*\"([^\"]+)\"").matcher(movieObj);
            if (durationMatcher.find()) duration = durationMatcher.group(1);
            
            // If we successfully grabbed an ID, build the UI card
            if (movieId != -1) {
                JPanel card = new JPanel(new BorderLayout(10, 10));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                card.setMaximumSize(new Dimension(500, 80));

                String infoText = String.format("<html><b>%s</b><br/>%s | %s</html>", movieName, genre, duration);
                card.add(new JLabel(infoText), BorderLayout.CENTER);
                
                JButton bookBtn = new JButton("Book Tickets");
                bookBtn.setBackground(new Color(255, 193, 7)); // Accent color
                
                // Pass the DYNAMIC data into the Booking window
                final int finalId = movieId;
                final String finalName = movieName;
                bookBtn.addActionListener(e -> {
                    new Booking(userId, finalId, finalName).setVisible(true);
                    dispose();
                });
                
                card.add(bookBtn, BorderLayout.EAST);
                panel.add(card);
                panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between cards
            }
        }
    }
}