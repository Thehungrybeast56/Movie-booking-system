from flask import Flask
from flask_cors import CORS
import os
# Import the db from models, and the api blueprint from routes
from models import db
from routes import api

app = Flask(__name__)
CORS(app)

base_dir = os.path.abspath(os.path.dirname(__file__))
db_path = os.path.join(base_dir, "movie_booking.db")

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + db_path
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# Connect the db object to the app
db.init_app(app)

with app.app_context():
    db.create_all()

# Register the routes blueprint
app.register_blueprint(api)

if __name__ == '__main__':
    app.run(debug=True)