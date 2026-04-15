from flask import Flask, request, jsonify
from flask_cors import CORS
import os
# IMPORT THE DB AND THE USER MODEL FROM MODELS.PY
from models import db, User, Movie, Show # ADDED THIS LINE TO IMPORT THE MOVIE AND SHOW MODELS

app = Flask(__name__)
CORS(app)

base_dir = os.path.abspath(os.path.dirname(__file__))
db_path = os.path.join(base_dir, "movie_booking.db")

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + db_path
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# This connects the db object to your app
db.init_app(app)

with app.app_context():
    db.create_all()

@app.route('/')
def home():
    return "Backend is connected to Database!"

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    new_user = User(name=data['name'], email=data['email'], password=data['password'])
    try:
        db.session.add(new_user)
        db.session.commit()
        return jsonify({"message": "User registered successfully!"}), 201
    except Exception as e:
        return jsonify({"message": "Registration failed", "error": str(e)}), 400

@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    user = User.query.filter_by(email=data['email'], password=data['password']).first()
    if user:
        return jsonify({"message": "Login successful!", "user_id": user.user_id}), 200
    return jsonify({"message": "Invalid email or password"}), 401

# Api to get all movies
@app.route('/movies', methods=['GET'])
def get_movies():
    all_movies = Movie.query.all()
    output = []
    for movie in all_movies:
        movie_data = {
            "movie_id": movie.movie_id,
            "movie_name": movie.movie_name,
            "genre": movie.genre,
            "duration": movie.duration
        }
        output.append(movie_data)
    return jsonify(output)

@app.route('/shows/<int:movie_id>', methods=['GET'])
def get_shows(movie_id):
    movie_shows = Show.query.filter_by(movie_id=movie_id).all()
    output = []
    for show in movie_shows:
        show_data = {
            "show_id": show.show_id,
            "time": show.time,
            "screen": show.screen
        }
        output.append(show_data)
    return jsonify(output)

if __name__ == '__main__':
    app.run(debug=True)