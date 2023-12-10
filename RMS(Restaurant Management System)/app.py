from flask import Flask, render_template, request, redirect, url_for, flash, abort
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_wtf import FlaskForm, CSRFProtect
from wtforms import StringField, PasswordField, SubmitField, HiddenField, SelectField, FloatField
from wtforms.validators import DataRequired, EqualTo, Length, ValidationError
from flask_login import LoginManager, UserMixin, login_user, login_required, logout_user, current_user
from werkzeug.security import check_password_hash, generate_password_hash
from functools import wraps

app = Flask(__name__)
app.config['SECRET_KEY'] = '88cd7caab05340c0bcf71a4308072096'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///site.db'
app.config['TESTING'] = True
app.config['SQLALCHEMY_DATABASE_URI_TESTING'] = 'sqlite:///test_site.db'  # Separate database for testing

if not app.config['TESTING']:
    db = SQLAlchemy(app)
    migrate = Migrate(app, db)
    csrf = CSRFProtect(app)

login_manager = LoginManager(app)
login_manager.login_view = 'login'

# Define SQLAlchemy models

class User(UserMixin, db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(100), nullable=False)
    is_admin = db.Column(db.Boolean, default=False)
    role = db.Column(db.String(20), nullable=False, default='user')
    # One-to-many relationship with Restaurant
    restaurants = db.relationship('Restaurant', backref='owner', lazy=True)

    def set_password(self, password):
        self.password = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password, password)

    def __repr__(self):
        return f"User('{self.username}')"

class Restaurant(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    location = db.Column(db.String(100), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    menu_items = db.relationship('Menu', backref='restaurant', lazy=True)

class Menu(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    restaurant_id = db.Column(db.Integer, db.ForeignKey('restaurant.id'), nullable=False)
    item_name = db.Column(db.String(100), nullable=False)
    price = db.Column(db.Float, nullable=False)

# Define FlaskForms
class RestaurantForm(FlaskForm):
    name = StringField('Name', validators=[DataRequired()])
    location = StringField('Location', validators=[DataRequired()])
    user_id = SelectField('User', coerce=int)
    submit = SubmitField('Add Restaurant')



class LoginForm(FlaskForm):
    username = StringField('Username', validators=[DataRequired()])
    password = PasswordField('Password', validators=[DataRequired()])
    submit = SubmitField('Login')

class RegistrationForm(FlaskForm):
    username = StringField('Username', validators=[DataRequired(), Length(min=4, max=50)])
    password = PasswordField('Password', validators=[DataRequired(), Length(min=6, max=100)])
    confirm_password = PasswordField('Confirm Password', validators=[DataRequired(), EqualTo('password')])
    submit = SubmitField('Register')

    def validate_username(self, field):
        if User.query.filter_by(username=field.data).first():
            raise ValidationError('Username is already taken. Please choose a different one.')
        
class EditRestaurantForm(FlaskForm):
    name = StringField('Name', validators=[DataRequired()])
    location = StringField('Location', validators=[DataRequired()])
    submit = SubmitField('Save Changes')

class DeleteRestaurantForm(FlaskForm):
    submit = SubmitField('Delete')

class MenuForm(FlaskForm):
    item_name = StringField('Item Name', validators=[DataRequired()])
    price = FloatField('Price', validators=[DataRequired()])
    submit = SubmitField('Add Item')

class EditMenuForm(FlaskForm):
    item_name = StringField('Item Name', validators=[DataRequired()])
    price = FloatField('Price', validators=[DataRequired()])
    submit = SubmitField('Save Changes')

class DeleteMenuForm(FlaskForm):
    submit = SubmitField('Delete')

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

def admin_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if current_user.role != 'admin':
            abort(403)  # Forbidden
        return f(*args, **kwargs)
    return decorated_function

@app.route('/admin/promote_user/<username>')
@login_required
def promote_user(username):
    if current_user.role != 'admin':
        abort(403)  # Forbidden

    user = User.query.filter_by(username=username).first()

    if user:
        # Toggle user role between 'user' and 'admin'
        user.role = 'admin' if user.role == 'user' else 'user'
        db.session.commit()

    return redirect(url_for('admin_dashboard'))


@app.route('/logout')
@login_required
def logout():
    logout_user()
    flash('You have been logged out.', 'info')
    return redirect(url_for('login'))

@app.route('/admin_dashboard')
@login_required
def admin_dashboard():
    if current_user.role != 'admin':
        abort(403)  # Forbidden

    users = User.query.all()
    restaurants = Restaurant.query.all()
    delete_restaurant_form = DeleteRestaurantForm()

    # Render admin dashboard
    return render_template('admin_dashboard.html', users=users, restaurants=restaurants, delete_restaurant_form=delete_restaurant_form)

@app.route('/dashboard')
@login_required
def dashboard():
    return render_template('dashboard.html', user=current_user)

@app.route('/')
def home():
    restaurants = Restaurant.query.all()
    return render_template('home.html', restaurants=restaurants)

@app.route('/add_restaurant', methods=['POST', 'GET'])
@login_required
def add_restaurant():
    form = RestaurantForm()

    # Populate user choices for admins
    if current_user.is_admin:
        form.user_id.choices = [(user.id, user.username) for user in User.query.all()]

    if form.validate_on_submit():
        name = form.name.data
        location = form.location.data

        # Get the user_id from the form data
        user_id = form.user_id.data if current_user.is_admin else current_user.id

        new_restaurant = Restaurant(name=name, location=location, user_id=user_id)
        db.session.add(new_restaurant)
        db.session.commit()

        flash('Restaurant added successfully!', 'success')
        return redirect(url_for('restaurant_list'))

    return render_template('add_restaurant.html', form=form)



@app.route('/edit_restaurant/<int:restaurant_id>', methods=['GET', 'POST'])
@login_required
def edit_restaurant(restaurant_id):
    restaurant = Restaurant.query.get(restaurant_id)

    form = EditRestaurantForm(obj=restaurant)

    # Check if the current user has permission to edit this restaurant
    if not current_user.is_admin and current_user.id != restaurant.user_id:
        flash('You do not have permission to edit this restaurant.', 'error')
        return redirect(url_for('restaurant_list'))

    if request.method == 'POST':
        restaurant.name = request.form['name']
        restaurant.location = request.form['location']

        # Update the owner if the current user is an admin
        if current_user.is_admin:
            new_owner_id = int(request.form['owner'])
            restaurant.user_id = new_owner_id

        db.session.commit()
        return redirect(url_for('restaurant_list'))

    return render_template('edit_restaurant.html', restaurant=restaurant, users=User.query.all(), form=form)


@app.route('/restaurant_list')
@login_required 
def restaurant_list():
    if current_user.is_admin:
        # Admin (you) can see all restaurants
        restaurants = Restaurant.query.all()
        is_admin = True
        delete_restaurant_form = DeleteRestaurantForm()
    else:
        # Non-admin users can only see their own added restaurants
        restaurants = Restaurant.query.filter_by(user_id=current_user.id).all()
        is_admin = False
        delete_restaurant_form = None

    return render_template('restaurant_list.html', restaurants=restaurants, is_admin=is_admin, delete_restaurant_form=delete_restaurant_form)

@app.route('/restaurant_detail/<int:restaurant_id>')
@login_required
def restaurant_detail(restaurant_id):
    restaurant = Restaurant.query.get(restaurant_id)
    return render_template('restaurant_detail.html', restaurant=restaurant)


@app.route('/delete_restaurant/<int:restaurant_id>', methods=['POST'])
@login_required
def delete_restaurant(restaurant_id):
    restaurant = Restaurant.query.get(restaurant_id)

    if current_user.is_admin:
        db.session.delete(restaurant)
        db.session.commit()
        return redirect(url_for('restaurant_list'))
    else:
        # User does not have permission to delete restaurants
        flash('You do not have permission to delete restaurants.', 'error')

    return redirect(url_for('restaurant_list'))


@app.route('/register', methods=['GET', 'POST'])
def register():
    if current_user.is_authenticated:
        return redirect(url_for('home'))

    form = RegistrationForm()

    if form.validate_on_submit():
        username = form.username.data
        password = form.password.data

        # Check if an admin user already exists
        admin_exists = User.query.filter_by(is_admin=True).first()

        # Assign role based on whether an admin exists
        role = 'admin' if not admin_exists else 'user'

        new_user = User(username=username, password=generate_password_hash(password), role=role, is_admin=(role == 'admin'))
        db.session.add(new_user)
        db.session.commit()

        flash('Your account has been created! You can now log in.', 'success')
        return redirect(url_for('login'))

    return render_template('register.html', form=form)

@app.route('/add_menu/<int:restaurant_id>', methods=['GET', 'POST'])
@login_required
def add_menu(restaurant_id):
    restaurant = Restaurant.query.get(restaurant_id)

    # Check if the current user has permission to add menu items for this restaurant
    if not current_user.is_admin and current_user.id != restaurant.user_id:
        flash('You do not have permission to add menu items for this restaurant.', 'error')
        return redirect(url_for('restaurant_list'))

    form = MenuForm()

    if form.validate_on_submit():
        item_name = form.item_name.data
        price = form.price.data

        new_menu_item = Menu(item_name=item_name, price=price, restaurant_id=restaurant_id)
        db.session.add(new_menu_item)
        db.session.commit()

        flash('Menu item added successfully!', 'success')
        return redirect(url_for('restaurant_list'))

    return render_template('add_menu.html', form=form, restaurant=restaurant)


@app.route('/edit_menu/<int:menu_id>', methods=['GET', 'POST'])
@login_required
def edit_menu(menu_id):
    menu_item = Menu.query.get(menu_id)
    restaurant = Restaurant.query.get(menu_item.restaurant_id)

    # Check if the current user has permission to edit this menu item
    if not current_user.is_admin and current_user.id != restaurant.user_id:
        flash('You do not have permission to edit this menu item.', 'error')
        return redirect(url_for('restaurant_list'))

    form = EditMenuForm(obj=menu_item)

    if form.validate_on_submit():
        menu_item.item_name = form.item_name.data
        menu_item.price = form.price.data

        db.session.commit()

        flash('Menu item updated successfully!', 'success')
        return redirect(url_for('restaurant_list'))

    return render_template('edit_menu.html', form=form, menu_item=menu_item, restaurant=restaurant)

@app.route('/delete_menu/<int:menu_id>', methods=['POST'])
@login_required
def delete_menu(menu_id):
    menu_item = Menu.query.get(menu_id)
    restaurant = Restaurant.query.get(menu_item.restaurant_id)

    # Check if the current user has permission to delete this menu item
    if not current_user.is_admin and current_user.id != restaurant.user_id:
        flash('You do not have permission to delete this menu item.', 'error')
        return redirect(url_for('restaurant_list'))

    db.session.delete(menu_item)
    db.session.commit()

    flash('Menu item deleted successfully!', 'success')
    return redirect(url_for('restaurant_list'))

@app.route('/menu_list/<int:restaurant_id>')
@login_required
def menu_list(restaurant_id):
    # Retrieve menu items for the specified restaurant_id
    menu_items = Menu.query.filter_by(restaurant_id=restaurant_id).all()
    # Implement your logic here
    restaurant = Restaurant.query.get_or_404(restaurant_id)
    delete_menu_form = DeleteMenuForm()  # Assuming you have a form named DeleteMenuForm

    return render_template('menu_list.html', restaurant=restaurant, menu_items=menu_items, delete_menu_form=delete_menu_form)



if __name__ == '__main__':
    app.run(debug=True, port=5000)
