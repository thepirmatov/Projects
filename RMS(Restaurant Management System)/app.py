from flask import Flask, render_template, request, redirect, url_for, flash
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_wtf import FlaskForm, CSRFProtect
from wtforms import StringField, PasswordField, SubmitField
from wtforms.validators import DataRequired
from flask_login import LoginManager, UserMixin, login_user, login_required, logout_user, current_user
from werkzeug.security import check_password_hash


app = Flask(__name__)
app.config['SECRET_KEY'] = '88cd7caab05340c0bcf71a4308072096'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///site.db'  # Use SQLite for simplicity
db = SQLAlchemy(app)
migrate = Migrate(app, db)
login_manager = LoginManager(app)
login_manager.login_view = 'login'
csrf = CSRFProtect()

# Define SQLAlchemy models
class Restaurant(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    location = db.Column(db.String(100), nullable=False)

class Menu(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    restaurant_id = db.Column(db.Integer, db.ForeignKey('restaurant.id'), nullable=False)
    item_name = db.Column(db.String(100), nullable=False)
    price = db.Column(db.Float, nullable=False)


# Define FlaskForms
class RestaurantForm(FlaskForm):
    name = StringField('Name', validators=[DataRequired()])
    location = StringField('Location', validators=[DataRequired()])
    submit = SubmitField('Add Restaurant')

class User(UserMixin, db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(100), nullable=False)

class LoginForm(FlaskForm):
    username = StringField('Username', validators=[DataRequired()])
    password = PasswordField('Password', validators=[DataRequired()])
    submit = SubmitField('Login')

csrf.init_app(app)

# Flask-Login user loader
@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))

# Routes
@app.route('/login', methods=['GET', 'POST'])
def login():
    form = LoginForm()

    if form.validate_on_submit():
        username = form.username.data
        password = form.password.data

        user = User.query.filter_by(username=username).first()

        if user and check_password_hash(user.password, password):
            login_user(user)
            flash('Login successful!', 'success')
            next_page = request.args.get('next') or url_for('dashboard')
            return redirect(next_page)

        flash('Invalid username or password', 'error')

    return render_template('login.html', form=form)

@app.route('/logout')
@login_required
def logout():
    logout_user()
    flash('You have been logged out.', 'info')
    return redirect(url_for('login'))

@app.route('/dashboard')
@login_required
def dashboard():
    return render_template('dashboard.html', user=current_user)


@app.route('/')
def home():
    restaurants = Restaurant.query.all()
    return render_template('home.html', restaurants=restaurants)

@app.route('/add_restaurant', methods=['POST', 'GET'])
def add_restaurant():
    form = RestaurantForm()

    if form.validate_on_submit():
        # Form is submitted and validated
        name = form.name.data
        location = form.location.data
        
        new_restaurant = Restaurant(name=name, location=location)
        db.session.add(new_restaurant)
        db.session.commit()

        flash('Restaurant added successfully!', 'success')
        return redirect(url_for('restaurant_list'))

    return render_template('add_restaurant.html', form=form)

@app.route('/edit_restaurant/<int:restaurant_id>', methods=['GET', 'POST'])
def edit_restaurant(restaurant_id):
    restaurant = Restaurant.query.get(restaurant_id)
    
    if request.method == 'POST':
        restaurant.name = request.form['name']
        restaurant.location = request.form['location']
        db.session.commit()
        return redirect(url_for('restaurant_list'))
    
    return render_template('edit_restaurant.html', restaurant=restaurant)


@app.route('/restaurant_list')
def restaurant_list():
    restaurants = Restaurant.query.all()
    #print("Number of restaurants:", len(restaurants))
    return render_template('restaurant_list.html', restaurants=restaurants)


@app.route('/delete_restaurant/<int:restaurant_id>', methods=['POST'])
def delete_restaurant(restaurant_id):
    restaurant = Restaurant.query.get(restaurant_id)
    db.session.delete(restaurant)
    db.session.commit()
    return redirect(url_for('restaurant_list'))


if __name__ == '__main__':
    app.run(debug=True, port=5000)
