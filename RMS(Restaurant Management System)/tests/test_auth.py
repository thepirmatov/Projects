import pytest
from flask_testing import TestCase
from app import app, db, User

class TestAuth(TestCase):
    def create_app(self):
        app.config['TESTING'] = True
        app.config['WTF_CSRF_ENABLED'] = False
        app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///test.db'
        return app

    def setUp(self):
        db.create_all()

    def tearDown(self):
        db.session.remove()
        db.drop_all()

    def create_user(self, username, password):
        user = User(username=username, password=password)
        db.session.add(user)
        db.session.commit()

    def login(self, username, password):
        return self.client.post('/login', data=dict(
            username=username,
            password=password
        ), follow_redirects=True)

    def test_registration(self):
        response = self.client.get('/register')
        assert response.status_code == 200
        assert b'Register' in response.data

        # Register a new user
        response = self.client.post('/register', data=dict(
            username='newuser',
            password='testpassword',
            confirm_password='testpassword'
        ), follow_redirects=True)

        # Check if the user is redirected to the login page after successful registration
        assert response.status_code == 200
        assert b'Login' in response.data

    def test_login_success(self):
        # Create and register a user
        self.create_user('testuser', 'testpassword')

        # Log in the user
        response = self.login('testuser', 'testpassword')

        assert b'Welcome to the Dashboard, ' in response.data
        assert b'testuser!' in response.data

    def test_logout(self):
        # Create and register a user
        self.create_user('testuser', 'testpassword')

        # Log in the user
        self.login('testuser', 'testpassword')

        # Log out the user
        response = self.client.get('/logout', follow_redirects=True)

        assert b'You have been logged out' in response.data

if __name__ == '__main__':
    pytest.main()
