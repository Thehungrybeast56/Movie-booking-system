# 🎬 Movie Booking System 

## 📖 Project Overview
The Movie Booking System is a full-stack digital platform that allows users to easily view showtimes and book movie tickets[cite: 10]. This application was developed as a comprehensive second-year B.Tech academic project to demonstrate the integration of frontend development, backend logic, database management, and API communication[cite: 10]. 

The system utilizes a modern, decoupled architecture, combining Java for the graphical user interface (GUI) and Python for backend processing and database management[cite: 10]. 

---

## 🛠️ Technologies Used
* **Frontend GUI:** Java (Swing)[cite: 8]
* **Backend API:** Python (Flask)[cite: 8]
* **Database:** SQLite (managed via SQLAlchemy ORM)[cite: 8]
* **Communication:** RESTful JSON APIs[cite: 8]

---

## ✨ System Features

### User Features
* User registration and authentication[cite: 8]
* View dynamically loaded available movies[cite: 8]
* View specific show timings and screens[cite: 8]
* Interactive seating layout for seat selection[cite: 8]
* Payment gateway simulation and total price calculation[cite: 8]
* Digital ticket generation and booking confirmation[cite: 8]

### Admin Features
* Add new movies to the database[cite: 8]
* Update existing movie details[cite: 8]
* Delete movies from the system[cite: 8]
* Manage and schedule show timings[cite: 8]
* View all system booking records[cite: 8]

---

## ⚙️ Architecture & Workflow

The application follows a strict Client-Server architecture pattern:
`User (Java GUI) -> API Request -> Python Backend (Flask) -> Database (SQLite)`[cite: 8]

**Booking Process Flow:**
`Login -> View Movies -> Select Movie -> Select Show -> Select Seats -> Confirm Booking -> Payment -> Ticket Generated`[cite: 8]

---

## 📁 Project Structure
movie-booking-system/
├── backend-python/
│   ├── add_movies_2.py
│   ├── app_2.py
│   ├── models_2.py
│   ├── routes.py
│   └── test_api_2.py
├── java-frontend/
│   ├── Booking.java
│   ├── Login.java
│   ├── Movies.java
│   └── Register.java
├── database/
│   └── movie_booking_3.db
└── README.md


---

## 🚀 How to Run the Project

### 1. Start the Backend Server
1. Open a terminal and navigate to the `backend-python` directory.
2. Install the required Python libraries: `pip install flask flask-cors flask-sqlalchemy requests`
3. Run the database seed script (first time only) to add sample movies: `python add_movies_2.py`
4. Start the Flask server: `python app_2.py`
5. The server will run locally on `http://127.0.0.1:5000`. Leave this terminal open.

### 2. Launch the Frontend Application
1. Open a second terminal window and navigate to the `java-frontend` directory.
2. Compile all Java source files: `javac *.java`
3. Run the main application entry point: `java Login`
