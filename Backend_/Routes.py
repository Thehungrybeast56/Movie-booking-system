from flask import Blueprint, request, jsonify
from models import db, User, Movie, Show, Booking, Seat

# Create a Blueprint object to organize your routes
api = Blueprint('api', __name__)

# ==========================================
# PUBLIC / USER ROUTES
# ==========================================

@api.route('/')
def home():
    return "Backend is connected to Database!"

@api.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    new_user = User(name=data['name'], email=data['email'], password=data['password'])
    try:
        db.session.add(new_user)
        db.session.commit()
        return jsonify({"message": "User registered successfully!"}), 201
    except Exception as e:
        return jsonify({"message": "Registration failed", "error": str(e)}), 400

@api.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    user = User.query.filter_by(email=data['email'], password=data['password']).first()
    if user:
        return jsonify({"message": "Login successful!", "user_id": user.user_id}), 200
    return jsonify({"message": "Invalid email or password"}), 401

@api.route('/movies', methods=['GET'])
def get_movies():
    all_movies = Movie.query.all()
    output = [{"movie_id": m.movie_id, "movie_name": m.movie_name, "genre": m.genre, "duration": m.duration} for m in all_movies]
    return jsonify(output)

@api.route('/shows/<int:movie_id>', methods=['GET'])
def get_shows(movie_id):
    movie_shows = Show.query.filter_by(movie_id=movie_id).all()
    output = [{"show_id": s.show_id, "time": s.time, "screen": s.screen} for s in movie_shows]
    return jsonify(output)

@api.route('/seats/<int:show_id>', methods=['GET'])
def get_seats(show_id):
    show_seats = Seat.query.filter_by(show_id=show_id).all()
    output = [{"seat_id": s.seat_id, "seat_number": s.seat_number, "status": s.status} for s in show_seats]
    return jsonify(output)

@api.route('/book', methods=['POST'])
def book_ticket():
    data = request.get_json()
    show_id = data.get('show_id')
    requested_seat_numbers = data.get('seats') # Expects a list, e.g., ["A1", "A2"]
    
    # 1. Fetch requested seats from the database
    seats = Seat.query.filter(Seat.show_id == show_id, Seat.seat_number.in_(requested_seat_numbers)).all()
    
    if len(seats) != len(requested_seat_numbers):
        return jsonify({"message": "One or more invalid seats selected."}), 400
        
    # 2. Check if all requested seats are available
    for seat in seats:
        if seat.status != "Available":
            return jsonify({"message": f"Seat {seat.seat_number} is already booked."}), 400

    # 3. Create the Booking and Update Seat Status
    try:
        for seat in seats:
            seat.status = "Booked"
            
        new_booking = Booking(
            user_id=data.get('user_id'),
            show_id=show_id,
            seats=",".join(requested_seat_numbers),
            payment_status="Completed"
        )
        db.session.add(new_booking)
        db.session.commit()
        return jsonify({"message": "Booking successful!", "booking_id": new_booking.booking_id}), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({"message": "Booking failed", "error": str(e)}), 400

@api.route('/booking-history/<int:user_id>', methods=['GET'])
def get_history(user_id):
    history = Booking.query.filter_by(user_id=user_id).all()
    output = [{"booking_id": b.booking_id, "show_id": b.show_id, "seats": b.seats, "payment_status": b.payment_status} for b in history]
    return jsonify(output)


# ==========================================
# ADMIN ROUTES
# ==========================================

@api.route('/admin/movies', methods=['POST'])
def add_movie():
    data = request.get_json()
    new_movie = Movie(movie_name=data['movie_name'], genre=data['genre'], duration=data['duration'])
    try:
        db.session.add(new_movie)
        db.session.commit()
        return jsonify({"message": "Movie added successfully!"}), 201
    except Exception as e:
        return jsonify({"message": "Failed to add movie", "error": str(e)}), 400

@api.route('/admin/movies/<int:movie_id>', methods=['PUT'])
def update_movie(movie_id):
    movie = Movie.query.get_or_404(movie_id)
    data = request.get_json()
    
    movie.movie_name = data.get('movie_name', movie.movie_name)
    movie.genre = data.get('genre', movie.genre)
    movie.duration = data.get('duration', movie.duration)
    
    db.session.commit()
    return jsonify({"message": "Movie updated successfully!"})

@api.route('/admin/movies/<int:movie_id>', methods=['DELETE'])
def delete_movie(movie_id):
    movie = Movie.query.get_or_404(movie_id)
    try:
        db.session.delete(movie)
        db.session.commit()
        return jsonify({"message": "Movie deleted successfully!"})
    except Exception as e:
        return jsonify({"message": "Failed to delete movie", "error": str(e)}), 400

@api.route('/admin/shows', methods=['POST'])
def add_show():
    data = request.get_json()
    new_show = Show(movie_id=data['movie_id'], time=data['time'], screen=data['screen'])
    try:
        db.session.add(new_show)
        db.session.commit()
        return jsonify({"message": "Show added successfully!"}), 201
    except Exception as e:
        return jsonify({"message": "Failed to add show", "error": str(e)}), 400

@api.route('/admin/bookings', methods=['GET'])
def get_all_bookings():
    bookings = Booking.query.all()
    output = [{"booking_id": b.booking_id, "user_id": b.user_id, "show_id": b.show_id, "seats": b.seats, "payment_status": b.payment_status} for b in bookings]
    return jsonify(output)