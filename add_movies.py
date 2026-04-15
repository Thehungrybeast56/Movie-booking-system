from app import app, db
from models import Movie

with app.app_context():
    m1 = Movie(movie_name="Inception", genre="Sci-Fi", duration="148 min")
    m2 = Movie(movie_name="The Dark Knight", genre="Action", duration="152 min")
    db.session.add(m1)
    db.session.add(m2)
    db.session.commit()
    print("Movies added!")