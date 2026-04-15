from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
import os
from flask import request, jsonify
from models import User

app = Flask(__name__)
CORS(app) # This will allow your Java app to talk to Python later

# This tells Flask where your database file is located
# It looks for the folder you named as 'database'
base_dir = os.path.abspath(os.path.dirname(__file__))
db_path = os.path.join(base_dir, "../database/movie_booking.db")

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + db_path
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

@app.route('/')
def home():
    return "Backend is connected to Database!"

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    # Create a new user object
    new_user = User(name=data['name'], email=data['email'], password=data['password'])

    try:
        db.session.add(new_user)# Add to database
        db.session.commit()     # Save changes
        return jsonify({"message": "User registered successfully!"}), 201
    except:
        return jsonify({"message": "User already exists or error occurred"}), 400
@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    #Search for user with matching email and password
    user = User.query.filter_by(email=data['email'], password=data['password']).first()
    
    if user:
        return jsonify({"message": "Login successful!", "user_id": user.user_id}), 200
    else:
        return jsonify({"message": "Invalid email or password"}), 401
    
if __name__ == '__main__':
    app.run(debug=True)
