from flask_sqlalchemy import SQLAlchemy

# We create the db object here
db = SQLAlchemy()

class Movie(db.Model):
    __tablename__ = 'Movies'
    movie_id = db.Column(db.Integer, primary_key=True)
    movie_name = db.Column(db.String(100))
    genre = db.Column(db.String(50))
    duration = db.Column(db.String(20))

class User(db.Model):
    __tablename__ = 'Users'
    user_id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100))
    email = db.Column(db.String(100), unique=True)
    password = db.Column(db.String(100))

class Show(db.Model):
    __tablename__ = 'Shows'
    show_id = db.Column(db.Integer, primary_key=True)
    movie_id = db.Column(db.Integer, db.ForeignKey('Movies.movie_id'))
    time = db.Column(db.String(20))
    screen = db.Column(db.String(20))

# Newly Added Seat Model
class Seat(db.Model):
    __tablename__ = 'Seats'
    seat_id = db.Column(db.Integer, primary_key=True)
    show_id = db.Column(db.Integer, db.ForeignKey('Shows.show_id'))
    seat_number = db.Column(db.String(10))
    status = db.Column(db.String(20))

# Updated Booking Model to match the DB schema
class Booking(db.Model):
    __tablename__ = 'Bookings'
    booking_id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('Users.user_id'))
    show_id = db.Column(db.Integer, db.ForeignKey('Shows.show_id'))
    seats = db.Column(db.String(200)) 
    payment_status = db.Column(db.String(50))